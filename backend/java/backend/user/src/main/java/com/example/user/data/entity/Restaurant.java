package com.example.user.data.entity;

import com.example.user.data.dto.RestaurantTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private String name;

    @Enumerated(EnumType.STRING)
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
    private String pictureUrl3;
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonIgnore
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @JsonIgnore
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
//    @Builder.Default
//    @OneToMany(mappedBy = "restaurant", fetch = FetchType.EAGER, orphanRemoval=true)
//    @Size(max = 100) // set max menu register size
////    @BatchSize(size = 100)
//    private List<Menu> menuList = new ArrayList<>();


}

// mapped super class
// table per class