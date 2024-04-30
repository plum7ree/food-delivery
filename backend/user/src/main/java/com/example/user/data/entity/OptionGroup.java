package com.example.user.data.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    String description;
        @Builder.Default
    Integer maxSelectNumber = 1;
            @Builder.Default
    boolean isNecessary = false;

    @OneToMany(mappedBy = "optionGroup", fetch = FetchType.EAGER, orphanRemoval=true)
    @Size(max=20)
                @Builder.Default
    List<Option> options = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="menu_id")
    Menu menu;


}
