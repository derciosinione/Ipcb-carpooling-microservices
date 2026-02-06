package pt.ipcb.carpooling.dto;

import lombok.Data;

public class LocationDto {

    @Data
    public static class LocationSuggestionResponse {
        private String displayName;
        private Double lat;
        private Double lon;
    }

    @Data
    public static class SaveUserLocationRequest {
        private String label;
        private Double lat;
        private Double lon;
    }
}
