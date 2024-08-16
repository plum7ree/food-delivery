package com.example.user.controller;

import com.example.user.data.dto.order.*;
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
import org.springframework.http.*;
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
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
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

    private final RestTemplate restTemplate;

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
        assert orderRequest.get("orderId") != null;
        assert orderRequest.get("restaurantId") != null;
        assert orderRequest.get("menus") != null;
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


        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONObject tossResponseJsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();

        /**
         /prepare 에서 저장했던, order 가져오기.
         {"orderId":"2d88fd82-143d-444e-aedf-f56da4a78fee",
         "menus":[
         {
         "menuId":"864293bc-cf74-42db-94f3-8c1ffeab9828",
         "quantity":1,
         "selectedOptions":[
         {
         "optionId":"4acc880e-61ce-4e84-9598-1ba676d721b9",
         "quantity":1
         },
         {
         "optionId":"4b739d9e-0f30-445c-8b81-a8186b52622d",
         "quantity":1
         }]}]
         ,"restaurantId":{}}
         **/
        String jsonStr = redisTemplate.opsForValue().get("order:" + orderId);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonStr);

        // User 정보 가져옴.
        var sub = Objects.requireNonNull(headers.get("X-Auth-User-Sub")).get(0);
        var accountEntity = accountService.getUserByOauth2Subject(sub);

        // 성공시 eats order service 에게 승인 요청해야함.
        String userId = accountEntity.get().getId();
        String restaurantId = jsonNode.get("restaurantId").asText();

        CreateOrderCommandDto createOrderCommandDto = CreateOrderCommandDto.builder()
            .callerId(UUID.fromString(userId))
            .calleeId(UUID.fromString(restaurantId))
            .price(new BigDecimal(amount))
            .address(createAddress(jsonNode))
            .payment(createPaymentDto(paymentKey))
            .items(createOrderItems(jsonNode))
            .orderId(UUID.fromString(orderId))
            .build();
        HttpHeaders orderRequestHeaders = new HttpHeaders();
        orderRequestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateOrderCommandDto> orderRequest = new HttpEntity<>(createOrderCommandDto, orderRequestHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
            orderServiceUri,
            HttpMethod.POST,
            orderRequest,
            String.class
        );

        return ResponseEntity.status(response.getStatusCode()).body((JSONObject) parser.parse(response.getBody()));
    }


    private AddressDto createAddress(JsonNode jsonNode) {
        // Address 생성 로직 구현
        return new AddressDto(UUID.randomUUID(), "", "", ""); // 실제 구현에 맞게 수정 필요
    }

    private PaymentDto createPaymentDto(String paymentKey) {
        // PaymentDto 생성 로직 구현
        return new PaymentDto(); // 실제 구현에 맞게 수정 필요
    }

    private List<OrderItemDto> createOrderItems(JsonNode jsonNode) {
        List<OrderItemDto> items = new ArrayList<>();
        JsonNode menusNode = jsonNode.get("menus");
        for (JsonNode menuNode : menusNode) {
            UUID menuId = UUID.fromString(menuNode.get("menuId").asText());
            int quantity = menuNode.get("quantity").asInt();

            List<OptionDto> options = new ArrayList<>();
            JsonNode optionsNode = menuNode.get("selectedOptions");
            for (JsonNode optionNode : optionsNode) {
                OptionDto option = OptionDto.builder()
                    .id(UUID.fromString(optionNode.get("optionId").asText()))
                    .cost(BigInteger.valueOf(optionNode.get("quantity").asLong()))
                    .build();
                options.add(option);
            }

            OrderItemDto orderItem = OrderItemDto.builder()
                .id(menuId)
                .quantity(quantity)
                .price(BigDecimal.ZERO) // 서버에서 계산
                .optionDtoList(options)
                .build();
            items.add(orderItem);
        }
        return items;
    }

    private String createOrderItemDtoString(JsonNode jsonNode) {
        StringBuilder orderItemJsonBuilder = new StringBuilder();
        JsonNode menusNode = jsonNode.get("menus");
        for (int i = 0; i < menusNode.size(); i++) {
            JsonNode menuNode = menusNode.get(i);
            String menuId = menuNode.get("menuId").asText();
            int quantity = menuNode.get("quantity").asInt();

            StringBuilder optionsJsonBuilder = new StringBuilder();
            JsonNode optionsNode = menuNode.get("selectedOptions");
            for (int j = 0; j < optionsNode.size(); j++) {
                JsonNode optionNode = optionsNode.get(j);
                String optionId = optionNode.get("optionId").asText();
                assert (optionId != null);
                int optionQuantity = optionNode.get("quantity").asInt();
                assert (optionQuantity > 0);
                optionsJsonBuilder.append(String.format("""
                    {
                        "id": "%s",
                        "name": "",
                        "cost": %d
                    }""", optionId, optionQuantity));

                if (j < optionsNode.size() - 1) {
                    optionsJsonBuilder.append(",");
                }
            }

            orderItemJsonBuilder.append(String.format("""
                {
                    "id": "%s",
                    "quantity": %d,
                    "price": 0,
                    "optionDtoList": [%s]
                }""", menuId, quantity, optionsJsonBuilder.toString()));

            if (i < menusNode.size() - 1) {
                orderItemJsonBuilder.append(",");
            }
        }

        return orderItemJsonBuilder.toString();
    }

}