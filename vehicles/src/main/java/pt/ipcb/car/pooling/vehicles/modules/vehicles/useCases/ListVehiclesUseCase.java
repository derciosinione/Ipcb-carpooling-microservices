package pt.ipcb.car.pooling.vehicles.modules.vehicles.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.contracts.response.VehicleResponse;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.mapper.VehicleMapper;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.repository.VehicleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListVehiclesUseCase {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public List<VehicleResponse> execute() {
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toResponse)
                .collect(Collectors.toList());
    }
}
