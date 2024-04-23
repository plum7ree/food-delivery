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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    boolean isDuplicatedAllowed = true;
    boolean isNecessary = false;

    @OneToMany(mappedBy = "optionGroup", fetch = FetchType.EAGER)
    @Size(max=20)
    List<Option> options = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="menu_id")
    Menu menu;


}
