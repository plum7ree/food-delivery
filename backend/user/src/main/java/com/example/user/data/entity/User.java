package com.example.user.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String role;
    // Getters and Setters

//    cascade: 이 속성은 연관된 엔티티의 상태 변화를 부모 엔티티에 전파할지 여부를 결정합니다. CascadeType 열거형을 사용하여 연관된 엔티티의 상태 변화를 지정할 수 있습니다. 주요한 옵션으로는 다음이 있습니다.
//    CascadeType.ALL: 모든 상태 변화를 전파합니다. 따라서 부모 엔티티가 저장, 업데이트, 삭제되면 연관된 모든 자식 엔티티도 해당 작업을 수행합니다.
//    CascadeType.PERSIST: 부모 엔티티가 저장될 때만 연관된 자식 엔티티가 저장됩니다.
//    CascadeType.MERGE: 부모 엔티티가 병합될 때만 연관된 자식 엔티티가 병합됩니다.
//    CascadeType.REMOVE: 부모 엔티티가 삭제될 때만 연관된 자식 엔티티가 삭제됩니다.
//    orphanRemoval: 이 속성은 부모-자식 관계에서 부모 엔티티가 자식 엔티티를 삭제할 때 자식 엔티티가 고아가 되는지 여부를 설정합니다. 즉, 부모 엔티티에서 제거된 자식 엔티티가 삭제되어야 하는지 여부를 결정합니다. 만약 true로 설정되면, 부모 엔티티에서 제거된 자식 엔티티는 데이터베이스에서도 삭제됩니다. 이 옵션을 사용하면 고아 엔티티를 방지할 수 있습니다.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Restaurant> restaurants;
}