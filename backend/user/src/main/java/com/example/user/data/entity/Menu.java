package com.example.user.data.entity;


import com.example.commondata.entity.BaseTimeValue;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String description;

    private String pictureUrl;

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

