package ru.liga.review.service;

import org.springframework.stereotype.Component;

@Component
public class DiscountFactory {

    public static DiscountService getInstance() {
        return new DiscountService();
    }
}
