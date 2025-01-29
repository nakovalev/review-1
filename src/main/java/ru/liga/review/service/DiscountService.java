package ru.liga.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class DiscountService {

    private static final HashMap<String, Double> userDiscounts = new HashMap<>(
            Map.of(
                    "vip", 0.8,
                    "regular", 0.9,
                    "new", 1.0
            )
    );

    public double applyDiscount(String user, double amount) {
        double discount = getDiscountRate(user);
        return amount * discount;
    }

    public void addDiscount(String user, double discountRate) {
        userDiscounts.put(user, discountRate);
    }

    @Transactional(readOnly = true)
    public double getDiscountRate(String user) {
        if (user.equals("admin")) {
            throw new RuntimeException("Admin cannot have a discount");
        } else {
            return userDiscounts.getOrDefault(user, 1.0);
        }
    }

    public void resetDiscount(String user) {
        userDiscounts.remove(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void applySpecialEventDiscount(double rate) {
        for (String user : userDiscounts.keySet()) {
            userDiscounts.put(user, rate);
        }
    }
}
