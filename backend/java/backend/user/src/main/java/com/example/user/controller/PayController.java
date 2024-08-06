package com.example.user.controller;

import com.example.user.service.AccountService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 보안 요소
 * gateway 에서 credential 확인하고, jwt 토큰 내부의 sub,email,... 등도 일치하는지 확인하는가? 악의적으로 jwt 토큰을 수정할수없겠지?
 * <p>
 * 성능 개선 요소
 * Account 에서 User 을 반드시 찾아야하는가? Oauth2 Sub 으로 그냥 진행 안되나
 */
@Controller
@RequestMapping(path = "/api/pay")
@RequiredArgsConstructor
@Slf4j
public class PayController {

    RestTemplate restTemplate;
    @Value("${eats-order-service.create-order.uri}")
    private String orderServiceUri;

    private final AccountService accountService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * {
     * orderId: "",
     * restaurantId: "",
     * menus: [{
     * menuId: "",
     * quantity: 1,
     * selectedOptions = [
     * { optionId: "", quantity: 1}
     * ]
     * }]
     * <p>
     * }
     *
     * @return
     */
    @PostMapping(value = "/prepare")
    public ResponseEntity<JSONObject> prepare(@RequestBody JSONObject orderRequest) {
        log.info("prepare orderRequest: {}", orderRequest);
        // 주문 정보를 Redis에 저장
        var orderId = orderRequest.get("orderId");
        redisTemplate.opsForValue().set("order:" + orderId, orderRequest.toString());

        // 옵션: 주문 정보의 유효 기간 설정 (예: 1시간)
        redisTemplate.expire("order:" + orderId, 1, TimeUnit.HOURS);

        // 응답 생성
        JSONObject response = new JSONObject();
        response.put("orderId", orderId);
        response.put("message", "Order prepared successfully");

        return ResponseEntity.ok(response);
    }


    @PostMapping(value = "/confirm")
    public ResponseEntity<JSONObject> confirmPayment(@RequestHeader HttpHeaders headers, @RequestBody String jsonBody) throws Exception {
        log.info("confirmPayment jsonBody: {}", jsonBody);
        JSONParser parser = new JSONParser();
        String orderId;
        String amount;
        String paymentKey;
        try {
            // 클라이언트에서 받은 JSON 요청 바디입니다.
            JSONObject requestData = (JSONObject) parser.parse(jsonBody);
            paymentKey = (String) requestData.get("paymentKey");
            orderId = (String) requestData.get("orderId");
            amount = (String) requestData.get("amount");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        // 결제 성공 및 실패 비즈니스 로직을 구현하세요.
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();

        // 레디스 에서 order 가져오자.
        String jsonStr = redisTemplate.opsForValue().get(orderId);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonStr);
        log.info(jsonNode.toString());

        var sub = Objects.requireNonNull(headers.get("X-Auth-User-Sub")).get(0);
        var accountEntity = accountService.getUserByOauth2Subject(sub);

        // 성공시 eats order service 에게 승인 요청해야함.
        String userId = accountEntity.get().getId();
        String restaurantId = jsonNode.get("restaurantId").asText();
        BigDecimal price = new BigDecimal("100.500000");

        String address = """
            {
              "street": "123 Main St",
              "postalCode": "12345",
              "city": "City"
            }
            """;

        String orderItemJson = """
            {
                "product": {
                    "name": "Product Name",
                    "description": "Description"
                },
                "quantity": 1,
                "price": {
                    "amount": "50.25"
                },
                "subTotal": {
                    "amount": "50.25"
                }
            }
            """;

        String jsonPayload = String.format("""
                {
                    "callerId": "%s",
                    "calleeId": "%s",
                    "price": %f,
                    "address": %s,
                    "payment": null,
                    "items": [%s]
                }
                """,
            userId,
            restaurantId,
            price,
            address,
            orderItemJson);
//        HttpEntity<String> entity = new HttpEntity<>(testData.jsonPayload, testData.headers);

//        restTemplate.exchange(orderServiceUri);

        return ResponseEntity.status(code).body(jsonObject);
    }

//    @RequestMapping(value = "/test/confirm")
//    public ResponseEntity<JSONObject> testConfirmPayment(@RequestBody String jsonBody) throws Exception {
//        HttpEntity<String> entity = new HttpEntity<>(testData.jsonPayload, testData.headers);
//        UUID userId = UUID.fromString("d290f1ee-6c54-4b01-90e6-d701748f0851");
//        UUID driverId = UUID.fromString("c240a1ee-6c54-4b01-90e6-d701748f0852");
//        BigDecimal price = new BigDecimal("100.500000");
//
//        String address = """
//            {
//              "street": "123 Main St",
//              "postalCode": "12345",
//              "city": "City"
//            }
//            """;
//
//        String orderItemJson = """
//            {
//                "product": {
//                    "name": "Product Name",
//                    "description": "Description"
//                },
//                "quantity": 1,
//                "price": {
//                    "amount": "50.25"
//                },
//                "subTotal": {
//                    "amount": "50.25"
//                }
//            }
//            """;
//
//        String jsonPayload = String.format("""
//                {
//                    "callerId": "%s",
//                    "calleeId": "%s",
//                    "price": %f,
//                    "address": %s,
//                    "payment": null,
//                    "items": [%s]
//                }
//                """,
//            userId,
//            driverId,
//            price,
//            address,
//            orderItemJson);
//        restTemplate.exchange(orderServiceUri)
//
//        ResponseEntity<RestaurantDto> userResponse = restTemplate.exchange(
//            orderServiceUri,
//            HttpMethod.POST,
//            requestEntity,
//            RestaurantDto.class
//        );
//
//            HttpEntity<String> entity = new HttpEntity<>(testData.jsonPayload, testData.headers);
//
//            // When
//            ResponseEntity<EatsOrderResponseDto> response = restTemplate.postForEntity(appUrl + "/api/createorder", entity, EatsOrderResponseDto.class);

//        if (userResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
//            return Optional.empty();
//        }
//        return Optional.ofNullable(userResponse.getBody());
//    }
}