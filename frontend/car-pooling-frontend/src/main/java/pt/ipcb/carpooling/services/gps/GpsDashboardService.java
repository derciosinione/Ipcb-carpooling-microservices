package pt.ipcb.carpooling.services.gps;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.ipcb.carpooling.clients.GpsClient;
import pt.ipcb.carpooling.dto.LocationDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class GpsDashboardService {

    private final GpsClient gpsClient;

    public void saveUserLocation(String userId, String label, Double lat, Double lon) {
        if (userId == null || label == null || label.isBlank() || lat == null || lon == null) {
            return;
        }
        try {
            LocationDto.SaveUserLocationRequest request = new LocationDto.SaveUserLocationRequest();
            request.setLabel(label);
            request.setLat(lat);
            request.setLon(lon);
            gpsClient.saveRecentLocation(userId, request);
        } catch (Exception e) {
            log.warn("Could not save recent location for user {}: {}", userId, e.getMessage());
        }
    }
}
