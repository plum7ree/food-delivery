package com.example.calldataaccess.entity;

import com.example.commondata.domain.aggregate.valueobject.CallStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "calls")
public class CallEntity {


    @Id
    @NotNull
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;
    private UUID userId;
    private UUID driverId;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "call_schema.call_status_enum")
    private CallStatus callStatus;
    private String failureMessages;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallEntity that = (CallEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

