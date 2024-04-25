package com.example.user.data.entity;


import com.example.commondata.entity.BaseTimeValue;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "menus")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String description;

    private String pictureUrl;

    private BigInteger price;
    // 통화 코드는 ISO 4217 표준에 따라 문자열 형태로 저장
    private String currency;

    @ManyToOne
    @JoinColumn(name="restaurant_id")
    private Restaurant restaurant;


    @OneToMany(mappedBy = "menu", fetch = FetchType.EAGER)
    @BatchSize(size=20)
    @Size(max=20)
    List<OptionGroup> optionGroupList = new ArrayList<>();


    @Embedded
    private BaseTimeValue time;
}

