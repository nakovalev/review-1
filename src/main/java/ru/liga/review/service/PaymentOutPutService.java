package ru.liga.review.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.liga.review.dto.PaymentResponse;

import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentOutPutService {

    private static final Map<Integer, ResponseEntity<PaymentResponse>> responses = new HashMap<>();
    private static Integer sequence = 0;

    public Integer save(PaymentResponse paymentRecorded) {
        responses.put(++sequence, new ResponseEntity<>(paymentRecorded, HttpStatus.CREATED));
        return sequence;
    }

    public ResponseEntity<PaymentResponse> findById(Integer id) {
        return responses.get(id);
    }

}
