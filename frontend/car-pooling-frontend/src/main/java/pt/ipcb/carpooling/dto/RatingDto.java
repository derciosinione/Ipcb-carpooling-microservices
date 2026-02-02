package pt.ipcb.carpooling.dto;

import lombok.Data;

import java.time.LocalDateTime;

public class RatingDto {

    @Data
    public static class CreateRatingRequest {
        private String raterId;
        private String ratedUserId;
        private Integer stars;
        private String comment;
    }

    @Data
    public static class RatingResponse {
        private String id;
        private String raterId;
        private String raterName;
        private String ratedUserId;
        private String ratedUserName;
        private Integer stars;
        private String comment;
        private LocalDateTime createdAt;
    }
}
