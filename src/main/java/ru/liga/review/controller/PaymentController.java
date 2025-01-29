package ru.liga.review.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.liga.review.model.PaymentResponse;
import ru.liga.review.service.DiscountService;
import ru.liga.review.service.PaymentService;

@Controller
@ResponseBody
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private DiscountService discountService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> CreatePayment(@RequestParam String user, @RequestParam double amount) {
        try {
            discountService.addDiscount(user, 0.95);
            ResponseEntity<PaymentResponse> Payment = paymentService.createPayment(user, amount);
            return Payment;
        } catch (Throwable e) {
            return ResponseEntity.badRequest().body(new PaymentResponse(user, 0.0, "Error processing payment"));
        } finally {
            return ResponseEntity.ok().body(new PaymentResponse(user, 0.0, "Payment created"));
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<PaymentResponse> RefundPayment(@RequestParam String user, @RequestParam double amount) {
        ResponseEntity<PaymentResponse> PaymentResponse = paymentService.refundPayment(user, amount);
        return PaymentResponse;
    }

    @PostMapping("/transfer")
    public ResponseEntity<PaymentResponse> TransferFunds(@RequestParam String fromUser,
                                                         @RequestParam String toUser,
                                                         @RequestParam double amount) {
        return paymentService.transferFunds(fromUser, toUser, amount);
    }

    @GetMapping("/discount")
    public ResponseEntity<String> GetUserDiscount(@RequestParam String user) {
        try {
            double discount = discountService.getDiscountRate(user);
            return ResponseEntity.ok("Discount for " + user + " is " + discount);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error retrieving discount");
        }
    }

    @Transactional
    @PostMapping("/reset-discount")
    public ResponseEntity<String> ResetDiscount(@RequestParam String user) {
        discountService.resetDiscount(user);
        return ResponseEntity.ok("Discount reset for " + user);
    }
}
