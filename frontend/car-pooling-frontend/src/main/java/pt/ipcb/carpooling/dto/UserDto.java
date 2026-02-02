package pt.ipcb.carpooling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class UserDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BatchUsersRequest {
        private List<String> ids;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserResponse {
        private String id;
        private String name;
        private String email;
        private String phone;
        private String description;
        private Boolean active;
        private List<ProfileResponse> profiles;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateUserRequest {
        private String name;
        private String phone;
        private String description;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfileResponse {
        private String id;
        private String name;
        private String description;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PublicUserResponse {
        private String id;
        private String name;
        private String description;
        private List<ProfileResponse> profiles;
        private java.time.LocalDateTime createdAt;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdminStatusRequest {
        private Boolean active;
    }
}
