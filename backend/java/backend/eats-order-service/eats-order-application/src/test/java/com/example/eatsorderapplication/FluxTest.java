package com.example.eatsorderapplication;

import reactor.core.publisher.Flux;

import java.util.List;

public class FluxTest {

    public static void main(String[] args) {
        List<AddressDto> userLocations = List.of(
            new AddressDto("Location1", 10.0, 20.0),
            new AddressDto("Location2", 15.0, 25.0)
        );

        Flux<DriverDetailsDto> a = Flux.fromIterable(userLocations)
            .flatMap(addressDto -> {
                DriverDetailsDto driverDetailsDto = DriverDetailsDto.builder()
                    .name(addressDto.getName())  // 예시 필드
                    .longitude(addressDto.getLon())
                    .latitude(addressDto.getLat())
                    .build();
                return Flux.just(driverDetailsDto);
            });

        // Flux<DriverDetailsDto>를 소비하거나 구독하여 결과를 확인
        a.subscribe(System.out::println);
    }
}

class AddressDto {
    private String name;
    private double lon;
    private double lat;

    public AddressDto(String name, double lon, double lat) {
        this.name = name;
        this.lon = lon;
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }
}

class DriverDetailsDto {
    private String name;
    private double longitude;
    private double latitude;

    private DriverDetailsDto(Builder builder) {
        this.name = builder.name;
        this.longitude = builder.longitude;
        this.latitude = builder.latitude;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "DriverDetailsDto{" +
            "name='" + name + '\'' +
            ", longitude=" + longitude +
            ", latitude=" + latitude +
            '}';
    }

    public static class Builder {
        private String name;
        private double longitude;
        private double latitude;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public DriverDetailsDto build() {
            return new DriverDetailsDto(this);
        }
    }
}

