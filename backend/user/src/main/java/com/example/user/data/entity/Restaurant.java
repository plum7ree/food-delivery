package com.example.user.data.entity;

import com.example.user.data.dto.RestaurantTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "user_schema.restaurant_type_enum")
    private RestaurantTypeEnum type;

    @Column(columnDefinition = "TIME")
    @Temporal(TemporalType.TIME)
    private LocalTime openTime;

    @Column(columnDefinition = "TIME")
    @Temporal(TemporalType.TIME)
    private LocalTime closeTime;

    // private List<Menu>
    // address Address // address should be value class?
    // private List<Grade> grades

    private String pictureUrl1;
    private String pictureUrl2;

    private BigDecimal price;
    // 통화 코드는 ISO 4217 표준에 따라 문자열 형태로 저장
    private String currency;

    @Builder.Default
    @OneToMany(mappedBy = "restaurant", fetch = FetchType.EAGER)
    @Size(max = 100) // set max menu register size
//    @BatchSize(size = 100)
    private List<Menu> menuList = new ArrayList<>();



}

// mapped super class
// table per class