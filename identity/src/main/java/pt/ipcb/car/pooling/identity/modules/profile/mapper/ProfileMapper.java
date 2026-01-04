package pt.ipcb.car.pooling.identity.modules.profile.mapper;

import org.springframework.stereotype.Component;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.request.CreateProfileRequest;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.response.ProfileResponse;
import pt.ipcb.car.pooling.identity.modules.profile.entities.ProfileEntity;

@Component
public class ProfileMapper {

    public ProfileResponse toResponse(ProfileEntity entity) {
        return ProfileResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ProfileEntity toEntity(CreateProfileRequest request) {
        return ProfileEntity.builder()
                .name(request.name())
                .description(request.description())
                .build();
    }
}
