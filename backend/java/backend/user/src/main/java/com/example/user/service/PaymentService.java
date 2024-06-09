package com.example.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class PaymentService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private Map<String, Object> paymentResult;

    public PaymentService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> confirmTossPayment(String orderId, int amount, String paymentKey, String encryptedSecretKey) {
        String url = "https://api.tosspayments.com/v1/payments/confirm";

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("orderId", orderId);
        requestBody.add("amount", amount);
        requestBody.add("paymentKey", paymentKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", encryptedSecretKey);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        log.info(requestEntity.toString());
        Map<String, Object> response = restTemplate.postForObject(url, requestEntity, Map.class);
        return response;
    }

}