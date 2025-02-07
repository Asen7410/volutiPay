package com.tr.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import okhttp3.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.*;

public class VolutiPixPaymentClient {

    private static final String PFX_FILE_PATH = "C:/Users/admin/Desktop/VOLUTI_74_VOLUTI_CASH_IN_PROD_PHR/Certificados/VOLUTI/PROD/QRCODES-MTLS/VOLUTI_74.pfx";
    private static final String PFX_PASSWORD = "V0lut1@123*.*";
    private static final String BASE_URL = "https://api.pix.voluti.com.br";
    private static final String CLIENT_ID = "00011199458321455000199";
    private static final String CLIENT_SECRET = "mE5Y2EwODYtYzhkZi00OWE4LWE2YzktO";
    private static final String TRUSTSTORE_PASS = "changeit";
    private static final String TRUSTSTORE_PATH = "C:/Program Files/Java/jdk-1.8/jre/lib/security/cacerts";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static OkHttpClient createOkHttpClient() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try (FileInputStream fis = new FileInputStream(PFX_FILE_PATH)) {
            keyStore.load(fis, PFX_PASSWORD.toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, PFX_PASSWORD.toCharArray());

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream fis = new FileInputStream(TRUSTSTORE_PATH)) {
            trustStore.load(fis, TRUSTSTORE_PASS.toCharArray());
        }


        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        return new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0])
                .build();
    }

    public static String getToken(OkHttpClient okHttpClient) throws Exception {
        String url = BASE_URL + "/oauth/token";

        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("client_id", CLIENT_ID);
        jsonMap.put("client_secret", CLIENT_SECRET);
        jsonMap.put("grant_type", "client_credentials");
        String json = objectMapper.writeValueAsString(jsonMap);

        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Accept", "application/json")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String result = response.body().string();
            System.out.println("Token 接口响应体: " + result);

            Map<?, ?> map = objectMapper.readValue(result, Map.class);
            Object tokenObj = map.get("access_token");
            if (tokenObj == null) {
                throw new RuntimeException("获取 token 失败，响应中未找到 access_token");
            }
            return tokenObj.toString();
        }
    }


    static Map<String, Object> getCob(OkHttpClient okHttpClient,String token) throws IOException {
        String url = BASE_URL + "/cob";
        Map<String, Object> bodyMap = new HashMap<>();

        // 日历字段（定义过期时间）
        Map<String, Object> calendario = new HashMap<>();
        calendario.put("expiracao", 3600);
        bodyMap.put("calendario", calendario);

        // 付款人信息（CPF 版本）
        Map<String, Object> devedor = new HashMap<>();
        devedor.put("cpf", "12345678909");
        devedor.put("nome", "João Silva");
        bodyMap.put("devedor", devedor);

        // 付款金额
        Map<String, Object> valor = new HashMap<>();
        valor.put("original", String.format(Locale.US, "%.2f", 100.1));
        bodyMap.put("valor", valor);

        // Pix 密钥
        bodyMap.put("chave", "c873834c-ed18-4e63-8e5d-90190392b744");

        // 付款备注信息
        bodyMap.put("solicitacaoPagador", "Pagamento referente à compra 1234");

        // 附加信息
        List<Map<String, String>> infoAdicionais = new ArrayList<>();
        Map<String, String> info1 = new HashMap<>();
        info1.put("nome", "Observação");
        info1.put("valor", "Entregar na recepção");
        infoAdicionais.add(info1);

        bodyMap.put("infoAdicionais", infoAdicionais);

        // 转换为 JSON 字符串
        String json = objectMapper.writeValueAsString(bodyMap);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        // 发送请求并处理响应
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                // 读取错误信息
                String errorResponse = response.body() != null ? response.body().string() : "No response body";
                throw new IOException("请求失败，状态码：" + response.code() + "，响应内容：" + errorResponse);
            }

            // 解析响应内容
            String result = response.body().string();
            // 解析返回 JSON，提取 txid
            Map<?, ?> responseMap = objectMapper.readValue(result, Map.class);
            Map<String, Object> resMap = new HashMap<>();
            Object txid = responseMap.get("txid");
            Object location = responseMap.get("location");
            Object pixCopiaECola = responseMap.get("pixCopiaECola");
            resMap.put("txid", txid);
            resMap.put("location", location);
            resMap.put("pixCopiaECola", pixCopiaECola);

            System.out.println("txid: " + txid);
            System.out.println("location: " + location);
            System.out.println("pixCopiaECola: " + pixCopiaECola);

            generatePixQRCode(pixCopiaECola.toString(),
                    "C:/Users/admin/Desktop/qccode/pix_" + txid + ".png",
                    300,
                    300
            );
            System.out.println("二维码已生成到: C:/Users/admin/Desktop/qccode/pix_" + txid + ".png");
            return resMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void generatePixQRCode(String pixCopiaECola, String outputFilePath, int width, int height) throws Exception {
        // 1. 创建二维码内容的 BitMatrix
        BitMatrix bitMatrix = new MultiFormatWriter().encode(pixCopiaECola,
                BarcodeFormat.QR_CODE,
                width,
                height);

        // 2. 指定输出路径
        Path path = FileSystems.getDefault().getPath(outputFilePath);

        // 3. 将 BitMatrix 转为 PNG 图片并写入到文件
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }


    public static void main(String[] args) throws Exception {
        OkHttpClient client = createOkHttpClient();
        String token = getToken(client);

        getCob(client,token);
    }




}
