package pt.ipcb.carpooling.services.vehicles;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.carpooling.clients.VehicleClient;
import pt.ipcb.carpooling.dto.VehicleDto;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VehicleDashboardService {

    private final VehicleClient vehicleClient;

    public VehicleDto.VehicleResponse findVehicleById(String id) {
        if (id == null) {
            return null;
        }
        List<VehicleDto.VehicleResponse> vehicles = vehicleClient.getAllVehicles();
        return vehicles.stream().filter(v -> Objects.equals(v.getId(), id)).findFirst().orElse(null);
    }
}
