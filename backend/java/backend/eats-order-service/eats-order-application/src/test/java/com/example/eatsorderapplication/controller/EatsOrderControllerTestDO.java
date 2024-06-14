package com.example.eatsorderapplication.controller;

import com.example.commondata.domain.aggregate.valueobject.*;
import com.example.eatsorderapplication.service.EatsOrderCommandService;
import com.example.eatsorderdomain.data.aggregate.OrderDomainObject;
import com.example.eatsorderdomain.data.dto.CreateOrderCommandDto;
import com.example.eatsorderdomain.data.event.CallCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// MockBean 의 철학 때문에
// Controller 층 밖에 테스트가 안되는듯? EatsOrderCommandService 을 @InjectMocks 로 하면 Controller 에 주입이 안됨.
// 따라서 @Spy 를 쓰는게 해결일수도? 하지만 권장되지는 않는듯?
// https://stackoverflow.com/questions/6300439/multiple-levels-of-mock-and-injectmocks

// eatsOrderController -> eatsOrderCommandServiceInjected
// -> CreateCallCommandHandler -> RestaurantApprovalRequestKafkaProducer, RestaurantApprovalRequestKafkaProducer
@WebMvcTest(controllers = {EatsOrderController.class})
public class EatsOrderControllerTestDO {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private EatsOrderCommandService eatsOrderCommandServiceInjected;



    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void printEndpointMappings() throws Exception {
        handlerMapping.getHandlerMethods().forEach((key, value) -> {
            System.out.println(key + " : " + value);
        });
        //{ [/error]} : org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController#error(HttpServletRequest)
        //{GET [/api/ok], produces [application/json]} : com.example.eatsorderapplication.controller.TestController#ok()
        //{POST [/api/ok], produces [application/json]} : com.example.eatsorderapplication.controller.TestController#okPost()
        //{ [/error], produces [text/html]} : org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController#errorHtml(HttpServletRequest, HttpServletResponse)
    }


    @Test
    void TestEatsOrderControllerTest() throws Exception {

        mockMvc.perform(post("/api/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


    }


    @Test
    public void testEatsOrderControllerKafkaPublishingData() throws Exception {
        // given
        UUID userId = UUID.fromString("d290f1ee-6c54-4b01-90e6-d701748f0851");
        UUID driverId = UUID.fromString("c240a1ee-6c54-4b01-90e6-d701748f0852");
        BigDecimal price = new BigDecimal("100.50");
        var street = "123 Main St";
        var postalCode = "12345";
        var city = "City";
        Address address = Address.builder()
                .street(street)
                .postalCode(postalCode)
                .city(city)
                .build();
        String addressJson = objectMapper.writeValueAsString(address);

        // adding " in "%s" is important!
        String jsonPayload = String.format("""
                        {
                            "userId": "%s",
                            "driverId": "%s",
                            "price": %f,
                            "address": %s,
                            "payment": null,
                            "route": null
                        }
                        """,
                userId,
                driverId,
                price,
                objectMapper.writeValueAsString(address));
        CreateOrderCommandDto commandDto = new CreateOrderCommandDto(
                userId,
                driverId,
                price,
                address,
                null,
                null
        );

        var trackingId = UUID.randomUUID();
        OrderDomainObject orderDomainObject = OrderDomainObject.builder()
                .calleeId(new CalleeId(driverId))
                .callerId(new CallerId(userId))
                .price(new Money(price))
            .orderStatus(OrderStatus.PENDING)
                .build();
        var now = ZonedDateTime.now(ZoneId.of("UTC"));

        CallCreatedEvent callCreatedEvent = new CallCreatedEvent(orderDomainObject, now);


        // when
        mockMvc.perform(post("/api/eatsorder")
                        .content(jsonPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        // 1. callRepository save should be called
        // 2. check kafka publish called
        // 3. check kafka publish content
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        var createEatsOrderCommandDto = CreateOrderCommandDto.builder()
                .address(address)
            .calleeId(driverId)
                .price(price)
            .callerId(userId)
                .payment(null)
                .build();

        // then
        ArgumentCaptor<CreateOrderCommandDto> captor = ArgumentCaptor.forClass(CreateOrderCommandDto.class);
        verify(eatsOrderCommandServiceInjected, times(1)).createAndPublishOrder(captor.capture());

        CreateOrderCommandDto capturedArgument = captor.getValue();

        assertThat(capturedArgument.getCallerId()).isEqualTo(userId);
        assertThat(capturedArgument.getCalleeId()).isEqualTo(driverId);
        assertThat(capturedArgument.getPrice()).isEqualByComparingTo(price);
        assertThat(capturedArgument.getAddress().getCity()).isEqualTo(city);
        assertThat(capturedArgument.getAddress().getPostalCode()).isEqualTo(postalCode);
        assertThat(capturedArgument.getAddress().getStreet()).isEqualTo(street);
        assertThat(capturedArgument.getPayment()).isNull();
    }

}