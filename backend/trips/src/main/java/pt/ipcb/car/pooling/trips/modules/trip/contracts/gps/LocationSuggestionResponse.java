package pt.ipcb.car.pooling.trips.modules.trip.contracts.gps;

import lombok.Data;

@Data
public class LocationSuggestionResponse {
    private String displayName;
    private Double lat;
    private Double lon;
}
