package pt.ipcb.car.pooling.vehicles.modules.vehicles.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.vehicles.modules.models.entities.ModelEntity;
import pt.ipcb.car.pooling.vehicles.modules.models.repository.ModelRepository;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.contracts.request.CreateVehicleRequest;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.contracts.response.VehicleResponse;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.entities.VehicleEntity;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.integration.IdentityClient;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.mapper.VehicleMapper;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.repository.VehicleRepository;

@Service
@RequiredArgsConstructor
public class CreateVehicleUseCase {

        private final VehicleRepository vehicleRepository;
        private final ModelRepository modelRepository;
        private final VehicleMapper vehicleMapper;
        private final IdentityClient identityClient;

        public VehicleResponse execute(CreateVehicleRequest request) {
                String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
                java.util.UUID userId = java.util.UUID.fromString(userIdStr);

                // Validate User via Feign Client
                try {
                        identityClient.getUserById(userId);
                } catch (Exception e) {
                        throw new RuntimeException("User verification failed: " + e.getMessage());
                }

                ModelEntity model = modelRepository.findById(request.getModelId())
                                .orElseThrow(() -> new RuntimeException("Model not found"));

                VehicleEntity vehicle = vehicleMapper.toEntity(request, model, userId);

                VehicleEntity savedVehicle = vehicleRepository.save(vehicle);

                return vehicleMapper.toResponse(savedVehicle);
        }
}
