package com.example.user.data.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionGroup {
    String description;
    @Builder.Default
    Integer maxSelectNumber = 1;
    @Builder.Default
    boolean isNecessary = false;
    @ManyToOne
    @JoinColumn(name = "menu_id")
    Menu menu;

    //    @OneToMany(mappedBy = "optionGroup", fetch = FetchType.EAGER, orphanRemoval=true)
//    @Size(max=20)
//                @Builder.Default
//    List<Option> options = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


}
