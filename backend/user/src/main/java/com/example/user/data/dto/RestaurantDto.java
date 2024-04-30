package com.example.user.data.dto;

import com.example.user.data.entity.Menu;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto {
    String id;
    String sessionId;
    String userId;
    String name;
    String type;

    LocalTime openTime;
    LocalTime closeTime;

    private String pictureUrl1;
    private String pictureUrl2;

    private List<MenuDto> menuDtoList;


}
