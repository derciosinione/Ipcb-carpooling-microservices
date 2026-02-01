package pt.ipcb.car.pooling.vehicles.modules.vehicles.mapper;

import org.springframework.stereotype.Component;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.contracts.request.CreateVehicleRequest;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.contracts.response.VehicleResponse;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.entities.VehicleEntity;

@Component
public class VehicleMapper {

    public VehicleResponse toResponse(VehicleEntity entity) {
        return VehicleResponse.builder()
                .id(entity.getId())
                .model(entity.getModel())
                .brandId(entity.getBrand().getId())
                .brandName(entity.getBrand().getName())
                .licensePlate(entity.getLicensePlate())
                .year(entity.getYear())
                .color(entity.getColor())
                .userId(entity.getUserId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public VehicleEntity toEntity(CreateVehicleRequest request,
            pt.ipcb.car.pooling.vehicles.modules.brands.entities.BrandEntity brand, java.util.UUID userId) {
        return VehicleEntity.builder()
                .model(request.getModel())
                .brand(brand)
                .licensePlate(request.getLicensePlate())
                .year(request.getYear())
                .color(request.getColor())
                .userId(userId)
                .build();
    }
}
