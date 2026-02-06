package pt.ipcb.carpooling.dto;

import lombok.Data;

import java.time.LocalDateTime;

public class NotificationDto {

    @Data
    public static class NotificationResponse {
        private String id;
        private String recipientUserId;
        private String title;
        private String message;
        private String type;
        private Boolean read;
        private LocalDateTime createdAt;
    }
}
