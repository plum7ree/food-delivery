package com.example.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TossPaymentTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPaymentConfirmation() throws Exception {
        // 테스트용 데이터 설정
        String paymentKey = "test_payment_key";
        String orderId = "test_order_id";
        int amount = 10000;

        // 가상의 요청 데이터 생성
        String requestBody = String.format("{\"paymentKey\":\"%s\",\"orderId\":\"%s\",\"amount\":%d}", paymentKey, orderId, amount);

        // 결제 확인 요청 보내기
        mockMvc.perform(MockMvcRequestBuilders.post("/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentKey").value(paymentKey))
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.amount").value(amount));

        // 결제 성공 페이지 요청 보내기
        mockMvc.perform(MockMvcRequestBuilders.get("/success")
                        .param("paymentKey", paymentKey)
                        .param("orderId", orderId)
                        .param("amount", String.valueOf(amount)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("결제 성공")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("주문번호: " + orderId)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("결제 금액: " + amount)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("paymentKey: " + paymentKey)));
    }
}