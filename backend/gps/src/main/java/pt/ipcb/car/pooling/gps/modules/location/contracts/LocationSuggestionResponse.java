package pt.ipcb.car.pooling.gps.modules.location.contracts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationSuggestionResponse {
    private String displayName;
    private Double lat;
    private Double lon;
}
