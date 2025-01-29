package ru.liga.review.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.liga.review.dto.PaymentResponse;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PaymentService {

    private static String lastUser;
    private static double lastAmount;

    @Autowired
    private final DiscountFactory df;
    private final PaymentOutPutService paymentOutPutService;

    private Map<String, Double> balances = new HashMap<>();

    public Integer createPayment(String user, double amount) {
        lastUser = user;
        lastAmount = amount;
        double discountedAmount = df.getInstance().applyDiscount(lastUser, lastAmount);
        balances.put(user, balances.getOrDefault(lastUser, 0.0) + discountedAmount);
        PaymentResponse paymentRecorded = new PaymentResponse(lastUser, discountedAmount, "Payment recorded");
        return paymentOutPutService.save(paymentRecorded);
    }

    @Transactional
    public ResponseEntity<PaymentResponse> refundPayment(String user, double amount) {
        if (!balances.containsKey(user) || balances.get(user) < amount) {
            return ResponseEntity.badRequest().body(new PaymentResponse(user, 0.0, "Insufficient funds"));
        } else {
            balances.put(user, balances.get(user) - amount);
            return ResponseEntity.ok(new PaymentResponse(user, amount, "Refund processed"));
        }
    }

    public ResponseEntity<PaymentResponse> transferFunds(String fromUser, String toUser, double amount) {
        if (!balances.containsKey(fromUser) || balances.get(fromUser) < amount) {
            return ResponseEntity.badRequest().body(new PaymentResponse(fromUser, 0.0, "Insufficient funds"));
        } else {
            balances.put(fromUser, balances.get(fromUser) - amount);
            balances.put(toUser, balances.getOrDefault(toUser, 0.0) + amount);
            return ResponseEntity.ok(new PaymentResponse(fromUser, amount, "Funds transferred to " + toUser));
        }
    }
}
