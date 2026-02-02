package pt.ipcb.carpooling.services.vehicles;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.ipcb.carpooling.clients.VehicleClient;
import pt.ipcb.carpooling.dto.VehicleDto;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleDashboardService {

    private final VehicleClient vehicleClient;

    public VehicleDto.VehicleResponse findVehicleById(String id) {
        if (id == null) {
            return null;
        }
        try {
            List<VehicleDto.VehicleResponse> vehicles = vehicleClient.getAllVehicles();
            return vehicles.stream().filter(v -> Objects.equals(v.getId(), id)).findFirst().orElse(null);
        } catch (Exception e) {
            log.error("Error loading vehicles: {}", e.getMessage());
            return null;
        }
    }
}
