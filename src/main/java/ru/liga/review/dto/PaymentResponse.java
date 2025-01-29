package ru.liga.review.dto;

public record PaymentResponse(String user,
                              double amount,
                              String message) {
}
