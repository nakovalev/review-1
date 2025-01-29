package ru.liga.review.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
        log.error(message);
    }
}
