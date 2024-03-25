package com.example.calldataaccess.entity;

import com.example.commondata.domain.aggregate.valueobject.CallStatus;
import jakarta.persistence.*;
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
@Table(name = "calls")
@Entity
public class CallEntity {


    @Id
    private UUID id;
    private UUID userId;
    private UUID driverId;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private CallStatus orderStatus;
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

