package pt.ipcb.carpooling.services.gps;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.carpooling.clients.GpsClient;
import pt.ipcb.carpooling.dto.LocationDto;

@Service
@RequiredArgsConstructor
public class GpsDashboardService {

    private final GpsClient gpsClient;

    public void saveUserLocation(String userId, String label, Double lat, Double lon) {
        if (userId == null || label == null || label.isBlank() || lat == null || lon == null) {
            return;
        }
        LocationDto.SaveUserLocationRequest request = new LocationDto.SaveUserLocationRequest();
        request.setLabel(label);
        request.setLat(lat);
        request.setLon(lon);
        gpsClient.saveRecentLocation(userId, request);
    }
}
