package com.example.eatsorderapplication;

import com.example.commondata.domain.aggregate.valueobject.*;
import com.example.eatsorderapplication.controller.EatsOrderController;
import com.example.eatsorderapplication.service.CreateCallCommandHandler;
import com.example.eatsorderapplication.service.CreateCallCommandManager;
import com.example.eatsorderapplication.service.EatsOrderCommandService;
import com.example.eatsorderapplication.service.publisher.kafka.RestaurantApprovalRequestKafkaProducer;
import com.example.eatsorderdataaccess.repository.CallRepository;
import com.example.eatsorderdomain.data.aggregate.Call;
import com.example.eatsorderdomain.data.dto.CreateEatsOrderCommandDto;
import com.example.eatsorderdomain.data.event.CallCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.math.BigDecimal;
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
public class EatsOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    CallRepository callRepository;

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
        Address address = Address.builder()
                .street("123 Main St")
                .postalCode("12345")
                .city("City")
                .build();
        String addressJson = objectMapper.writeValueAsString(address);

        // adding " in "%s" is important!
        String jsonPayload = String.format("""
                        {
                            "userId": "%s",
                            "driverId": "%s",
                            "price": %d,
                            "address": %s,
                            "payment": null,
                            "route": null
                        }
                        """,
                userId,
                driverId,
                price.toBigInteger(),
                objectMapper.writeValueAsString(address));
        CreateEatsOrderCommandDto commandDto = new CreateEatsOrderCommandDto(
                userId,
                driverId,
                price,
                address,
                null,
                null
        );

        var trackingId = UUID.randomUUID();
        Call call = Call.builder()
                .driverId(new DriverId(driverId))
                .userId(new UserId(userId))
                .price(new Money(price))
                .callStatus(CallStatus.PENDING)
                .build();

        CallCreatedEvent callCreatedEvent = call.updateIdsAndCreateEvent();


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

        var createEatsOrderCommandDto = CreateEatsOrderCommandDto.builder()
                .address(address)
                .driverId(driverId)
                .price(price)
                .userId(userId)
                .payment(null)
                .route(null)
                .build();

        // then
        ArgumentCaptor<CreateEatsOrderCommandDto> captor = ArgumentCaptor.forClass(CreateEatsOrderCommandDto.class);
        verify(eatsOrderCommandServiceInjected, times(1)).createCall(captor.capture());

        CreateEatsOrderCommandDto capturedArgument = captor.getValue();

        assertThat(capturedArgument.getUserId()).isEqualTo(userId);
        assertThat(capturedArgument.getDriverId()).isEqualTo(driverId);
        assertThat(capturedArgument.getPrice()).isEqualByComparingTo(price);
        assertThat(capturedArgument.getAddress()).isEqualTo(address);
        assertThat(capturedArgument.getPayment()).isNull();
        assertThat(capturedArgument.getRoute()).isNull();
    }

}