package com.example.eatsorderapplication.controller;

import com.example.commondata.domain.aggregate.valueobject.CalleeId;
import com.example.commondata.domain.aggregate.valueobject.CallerId;
import com.example.commondata.domain.aggregate.valueobject.Money;
import com.example.eatsorderapplication.utils.TestDataGenerator;
import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.eatsorderdomain.data.dto.CreateOrderRequest;
import com.example.eatsorderdomain.data.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
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
    private RestTemplate restTemplate;

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
        TestDataGenerator.TestData testData = TestDataGenerator.generateTestData();

        Order orderDomainObject = Order.builder()
            .calleeId(new CalleeId(testData.driverId))
            .callerId(new CallerId(testData.userId))
            .price(new Money(testData.price))
            .orderStatus(OrderStatus.PENDING)
            .build();

        var now = ZonedDateTime.now(ZoneId.of("UTC"));

        CallCreatedEvent callCreatedEvent = new CallCreatedEvent(orderDomainObject, now);

        // Mock RestTemplate response
        UserDto mockUserDto = new UserDto();
        mockUserDto.setId(testData.userId.toString());
        mockUserDto.setEmail("user@example.com");
        ResponseEntity<UserDto> mockResponseEntity = new ResponseEntity<>(mockUserDto, HttpStatus.OK);

        final String userServiceUrl = "http://localhost:8077";
        final String userInfoPath = "/api/info";
        when(restTemplate.exchange(
            eq(userServiceUrl + userInfoPath),
            eq(HttpMethod.GET),
            any(),
            eq(UserDto.class)
        )).thenReturn(mockResponseEntity);
        // when
        mockMvc.perform(post("/api/eatsorder")
                .content(testData.jsonPayload)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(testData.headers))
            .andExpect(status().isOk());
        // then
        // 1. callRepository save should be called
        // 2. check kafka publish called
        // 3. check kafka publish content
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);


        // then
        ArgumentCaptor<CreateOrderRequest> captor = ArgumentCaptor.forClass(CreateOrderRequest.class);
        verify(eatsOrderCommandServiceInjected, times(1)).createAndSaveOrder(captor.capture());

        CreateOrderRequest capturedArgument = captor.getValue();
        // Verify the captured argument
        assertEquals(testData.userId, capturedArgument.getCallerId());
        assertEquals(testData.driverId, capturedArgument.getCalleeId());
        assertEquals(testData.price, capturedArgument.getPrice());
        assertEquals(testData.address, objectMapper.writeValueAsString(capturedArgument.getAddress()));
        assertNull(capturedArgument.getPayment());

        // Verify that RestTemplate was called
        verify(restTemplate).exchange(
            eq(userServiceUrl + userInfoPath),
            eq(HttpMethod.GET),
            any(),
            eq(UserDto.class)
        );

    }
}