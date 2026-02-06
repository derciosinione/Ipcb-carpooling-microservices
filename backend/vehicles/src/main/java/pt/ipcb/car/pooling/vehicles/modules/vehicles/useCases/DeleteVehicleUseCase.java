package pt.ipcb.car.pooling.vehicles.modules.vehicles.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.vehicles.exceptions.ResourceNotFoundException;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.repository.VehicleRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteVehicleUseCase {

    private final VehicleRepository vehicleRepository;

    public void execute(UUID id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle not found");
        }
        vehicleRepository.deleteById(id);
    }
}
