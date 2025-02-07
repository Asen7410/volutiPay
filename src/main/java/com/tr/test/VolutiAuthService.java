package com.tr.test;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Objects;

public class VolutiAuthService {

    private static final String TOKEN_URL = "https://accounts.voluti.com.br/api/v2/oauth/token";
    private static final String CLIENT_ID = "0ced2d59-c3c1-45ce-af6b-70f4171ba8ec";
    private static final String CLIENT_SECRET = "4c381a1e-e051-48ca-8ec2-67c37fc99b8a";

    public static String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 使用 UriComponentsBuilder 构建 URL 编码的请求参数
        String body = UriComponentsBuilder.newInstance()
                .queryParam("grant_type", "client_credentials")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("client_secret", CLIENT_SECRET)
                .build()
                .encode()
                .toString()
                .substring(1); // 去掉开头的 "?"

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String accessToken = Objects.toString(response.getBody().get("access_token"), null);
                Integer expiresIn = (Integer) response.getBody().get("expires_in");

                System.out.println("✅ Access Token: " + accessToken);
                System.out.println("⏳ Expires In: " + expiresIn + " seconds");

                return accessToken;
            } else {
                System.err.println("⚠️ 获取 token 失败: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("❌ 发生错误: " + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        String token = getAccessToken();
        System.out.println("🔑 Token: " + token);
    }
}

