package com.tr.test;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class VolutiCashOutService {

    private static final String CASH_OUT_URL = "https://api.voluti.com.br/v1/pix/cash-out";

    public static void sendPixWithdrawal(String accessToken, String pixKey, String amount, String transactionId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("pixKey", pixKey);
        requestBody.put("amount", amount);
        requestBody.put("transactionId", transactionId);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(CASH_OUT_URL, HttpMethod.POST, request, String.class);
        System.out.println(response.getBody());
    }

   public static void main(String[] args) {
        /*String token = VolutiAuthService.getAccessToken();
        sendPixWithdrawal(token, "example@pix.com", "50.00", "txn-67890");*/

       System.out.println("1111");
    }
}
