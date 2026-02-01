package pt.ipcb.carpooling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class VehicleDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleRequest {
        private String brandId;
        private String model;
        private String licensePlate;
        private Integer year;
        private Integer seats;
        private String color;
        private String ownerId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleResponse {
        private String id;
        private String brandName;
        private String model;
        private String licensePlate;
        private Integer year;
        private Integer seats;
        private String color;
        private String ownerId;
        private boolean verified;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BrandResponse {
        private String id;
        private String name;
    }
}
