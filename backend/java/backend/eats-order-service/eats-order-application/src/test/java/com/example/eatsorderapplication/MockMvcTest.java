package com.example.eatsorderapplication;

import com.example.eatsorderapplication.controller.EatsOrderController;
import com.example.eatsorderapplication.service.EatsOrderCommandService;
import com.example.eatsorderdataaccess.repository.jpa.OrderRepository;
import com.example.eatsorderapplication.controller.TestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers={TestController.class, EatsOrderController.class})
public class MockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    // 이거 두개 추가하니깐 바로 404 에러 뜨네 여기도. 심지어 controller 를 제거 해도 문제가 생김.
    @MockBean
    EatsOrderCommandService eatsOrderCommandService;
    // 이거 두개 추가하니깐 바로 404 에러 뜨네 여기도
    @MockBean
    OrderRepository orderRepository;

        @Autowired
    private RequestMappingHandlerMapping handlerMapping;

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
    @DisplayName("ok 문자열을 body 에 담아서 응답한다.")
    @Test
    void TestTestControllerOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ok"))
                .andExpectAll(
                        status().isOk(),
                        MockMvcResultMatchers.content().string("ok")
                );

    }

    @Test
    void TestTestControllerOkPost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/ok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        MockMvcResultMatchers.content().string("ok")
                );
    }

        @Test
    void TestEatsOrderControllerTest() throws Exception {

        mockMvc.perform(post("/api/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

//        mockMvc.perform(post("/api/eatsorder")
//                        .content(jsonPayload)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());

    }
}