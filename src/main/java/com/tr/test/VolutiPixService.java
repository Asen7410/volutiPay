package com.tr.test;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class VolutiPixService {
    private static final String CASH_IN_URL = "https://api.voluti.com.br/v1/pix/cash-in";

    public static void createPixPayment(String accessToken, String pixKey, String amount, String transactionId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("pixKey", pixKey);
        requestBody.put("amount", amount);
        requestBody.put("transactionId", transactionId);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(CASH_IN_URL, HttpMethod.POST, request, String.class);
        System.out.println(response.getBody());
    }

    public static void main(String[] args) {
        String token = VolutiAuthService.getAccessToken();
        createPixPayment(token, "example@pix.com", "100.00", "txn-12345");
    }

}
