package pt.ipcb.car.pooling.vehicles.modules.vehicles.mapper;

import org.springframework.stereotype.Component;
import pt.ipcb.car.pooling.vehicles.modules.models.entities.ModelEntity;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.contracts.request.CreateVehicleRequest;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.contracts.response.VehicleResponse;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.entities.VehicleEntity;

@Component
public class VehicleMapper {

    public VehicleResponse toResponse(VehicleEntity entity) {
        return VehicleResponse.builder()
                .id(entity.getId())
                .modelId(entity.getModel().getId())
                .modelName(entity.getModel().getName())
                .brandName(entity.getModel().getBrand().getName())
                .licensePlate(entity.getLicensePlate())
                .year(entity.getYear())
                .color(entity.getColor())
                .userId(entity.getUserId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public VehicleEntity toEntity(CreateVehicleRequest request, ModelEntity model, java.util.UUID userId) {
        return VehicleEntity.builder()
                .model(model)
                .licensePlate(request.getLicensePlate())
                .year(request.getYear())
                .color(request.getColor())
                .userId(userId)
                .build();
    }
}
