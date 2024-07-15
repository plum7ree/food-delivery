package com.example.couponapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueRequestDto {
    //TODO userId field should be removed. this value should be from spring security usercontext
    private String userId;
    private Long couponId;


}
