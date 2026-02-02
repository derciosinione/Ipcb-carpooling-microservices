package pt.ipcb.car.pooling.identity.modules.user.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pt.ipcb.car.pooling.identity.modules.profile.mapper.ProfileMapper;
import pt.ipcb.car.pooling.identity.modules.user.contracts.request.RegisterUserRequest;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.PublicUserResponse;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.UserResponse;
import pt.ipcb.car.pooling.identity.modules.user.entities.UserEntity;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ProfileMapper profileMapper;

    public UserResponse toResponse(UserEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .name(entity.getName())
                .phone(entity.getPhone())
                .description(entity.getDescription())
                .active(entity.getActive())
                .profiles(entity.getProfiles() != null
                        ? entity.getProfiles().stream().map(profileMapper::toResponse).toList()
                        : List.of())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public PublicUserResponse toPublicResponse(UserEntity entity) {
        return new PublicUserResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getProfiles() != null
                        ? entity.getProfiles().stream().map(profileMapper::toResponse).toList()
                        : List.of(),
                entity.getCreatedAt());
    }

    public UserEntity toEntity(RegisterUserRequest request) {
        return UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .name(request.name())
                .phone(request.phone())
                .description(request.description())
                .build();
    }
}
