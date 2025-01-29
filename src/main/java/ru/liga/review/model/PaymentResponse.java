package ru.liga.review.model;

import lombok.Getter;

@Getter
public class PaymentResponse {
    public final String user;
    public final double amount;
    public final String message;

    public PaymentResponse(String user, double amount, String message) {
        this.user = user;
        this.amount = amount;
        this.message = message;
    }
}
