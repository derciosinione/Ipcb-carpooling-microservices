package pt.ipcb.car.pooling.vehicles.modules.models.mapper;

import org.springframework.stereotype.Component;
import pt.ipcb.car.pooling.vehicles.modules.brands.entities.BrandEntity;
import pt.ipcb.car.pooling.vehicles.modules.models.contracts.request.CreateModelRequest;
import pt.ipcb.car.pooling.vehicles.modules.models.contracts.response.ModelResponse;
import pt.ipcb.car.pooling.vehicles.modules.models.entities.ModelEntity;

@Component
public class ModelMapper {

    public ModelResponse toResponse(ModelEntity entity) {
        return ModelResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .brandId(entity.getBrand().getId())
                .brandName(entity.getBrand().getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ModelEntity toEntity(CreateModelRequest request, BrandEntity brand) {
        return ModelEntity.builder()
                .name(request.getName())
                .brand(brand)
                .build();
    }
}
