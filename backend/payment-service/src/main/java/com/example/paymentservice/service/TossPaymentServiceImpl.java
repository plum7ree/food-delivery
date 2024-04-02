//package com.example.paymentservice.service;
//
//import com.example.commondata.domain.aggregate.Payment;
//
//public class TossPaymentServiceImpl implements PaymentService {
//
//    private static final String TOSS_PAYMENTS_API_URL = "https://api.tosspayments.com/v1/payments/confirm";
//    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
//    private static final String AUTHORIZATION_HEADER = "Basic " + Base64.getEncoder().encodeToString((WIDGET_SECRET_KEY + ":").getBytes());
//
//    public PaymentEvent confirmPayment(Payment paymentDto) throws IOException {
//        String paymentKey = (String) requestBody.get("paymentKey");
//        String orderId = (String) requestBody.get("orderId");
//        int amount = (int) requestBody.get("amount");
//
//        OkHttpClient client = new OkHttpClient();
//        MediaType mediaType = MediaType.parse("application/json");
//        String requestBodyJson = new ObjectMapper().writeValueAsString(Map.of(
//                "orderId", orderId,
//                "amount", amount,
//                "paymentKey", paymentKey
//        ));
//
//        Request request = new Request.Builder()
//                .url(TOSS_PAYMENTS_API_URL)
//                .post(RequestBody.create(mediaType, requestBodyJson))
//                .addHeader("Authorization", AUTHORIZATION_HEADER)
//                .addHeader("Content-Type", "application/json")
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful()) {
//                // 결제 성공 비즈니스 로직을 구현하세요.
//                String responseBody = response.body().string();
//                System.out.println(responseBody);
//                return ResponseEntity.ok(responseBody);
//            } else {
//                // 결제 실패 비즈니스 로직을 구현하세요.
//                String errorBody = response.body().string();
//                System.out.println(errorBody);
//                return ResponseEntity.status(response.code()).body(errorBody);
//            }
//        }
//    }
//
//    public PaymentEvent cancelPayment() {
//
//    }
//
//}
