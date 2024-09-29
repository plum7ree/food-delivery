package com.example.commondata.dto.order;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserOrderAddressDto(
    @JsonProperty @NotNull String orderId,
    @JsonProperty @NotNull String userId,
    @JsonProperty @NotNull AddressDto address) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserOrderAddressDto that = (UserOrderAddressDto) o;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

}
