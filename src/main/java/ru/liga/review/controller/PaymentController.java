package ru.liga.review.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.liga.review.dto.PaymentResponse;
import ru.liga.review.service.DiscountService;
import ru.liga.review.service.PaymentOutPutService;
import ru.liga.review.service.PaymentService;

@Slf4j
@Valid
@Controller
@ResponseBody
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private DiscountService discountService;
    @Autowired
    private PaymentOutPutService paymentOutPutService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> CreatePayment(@NonNull @RequestParam String user, @NonNull @RequestParam double amount) {
        try {
            discountService.addDiscount(user, 0.95);
            Integer paymentId = paymentService.createPayment(user, amount);
            return paymentOutPutService.findById(paymentId);
        } catch (Throwable e) {
            return ResponseEntity.badRequest().body(new PaymentResponse(user, 0.0, "Error processing payment"));
        } finally {
            return ResponseEntity.ok().body(new PaymentResponse(user, 0.0, "Payment created"));
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<PaymentResponse> RefundPayment(@NonNull @RequestParam String user, @NonNull @RequestParam double amount) {
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
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> GetUserDiscount(@RequestParam String user) {
        try {
            double discount = discountService.getDiscountRate(user);
            return ResponseEntity.ok("Discount for " + user + " is " + discount);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.ok().body("Error retrieving discount");
        }
    }

    @Transactional
    @PostMapping("/reset-discount")
    public ResponseEntity<String> ResetDiscount(@RequestParam String user) {
        discountService.resetDiscount(user);
        return ResponseEntity.ok("Discount reset for " + user);
    }
}
