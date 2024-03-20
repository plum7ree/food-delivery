package com.example.driver.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
//import io.swagger.v3.oas.annotations.media.Schema;
//
//@Schema(
//        name="Response",
//        description="response info"
//)
@Data
@AllArgsConstructor
public class ResponseDto {
    private String statusCode;
    private String statusMsg;
}
