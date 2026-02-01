package pt.ipcb.car.pooling.vehicles.modules.brands.mapper;

import org.springframework.stereotype.Component;
import pt.ipcb.car.pooling.vehicles.modules.brands.contracts.request.CreateBrandRequest;
import pt.ipcb.car.pooling.vehicles.modules.brands.contracts.response.BrandResponse;
import pt.ipcb.car.pooling.vehicles.modules.brands.entities.BrandEntity;

@Component
public class BrandMapper {

    public BrandResponse toResponse(BrandEntity entity) {
        return BrandResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public BrandEntity toEntity(CreateBrandRequest request) {
        return BrandEntity.builder()
                .name(request.name())
                .description(request.description())
                .build();
    }
}
