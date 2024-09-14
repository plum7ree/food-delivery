package com.example.eatsorderapplication.utils;


import com.example.commondata.domain.aggregate.valueobject.Address;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;

import java.math.BigDecimal;
import java.util.UUID;

public class TestDataGenerator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static class TestData {
        public final UUID userId;
        public final UUID driverId;
        public final BigDecimal price;
        public final String address;
        public final String jsonPayload;
        public final HttpHeaders headers;

        private TestData(UUID userId, UUID driverId, BigDecimal price, String address, String jsonPayload, HttpHeaders headers) {
            this.userId = userId;
            this.driverId = driverId;
            this.price = price;
            this.address = address;
            this.jsonPayload = jsonPayload;
            this.headers = headers;
        }
    }

    public static TestData generateTestData() throws JsonProcessingException {
        UUID userId = UUID.fromString("d290f1ee-6c54-4b01-90e6-d701748f0851");
        UUID driverId = UUID.fromString("c240a1ee-6c54-4b01-90e6-d701748f0852");
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
            driverId,
            price,
            address,
            orderItemJson);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        return new TestData(userId, driverId, price, address, jsonPayload, headers);
    }
}