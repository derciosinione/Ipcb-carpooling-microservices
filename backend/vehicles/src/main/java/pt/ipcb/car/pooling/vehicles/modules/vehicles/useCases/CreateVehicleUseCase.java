package pt.ipcb.car.pooling.vehicles.modules.vehicles.useCases;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.vehicles.modules.brands.entities.BrandEntity;
import pt.ipcb.car.pooling.vehicles.modules.brands.repository.IBrandRepository;
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
        private final IBrandRepository brandRepository;
        private final VehicleMapper vehicleMapper;
        private final IdentityClient identityClient;

        public VehicleResponse execute(CreateVehicleRequest request) {
                String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
                java.util.UUID userId = java.util.UUID.fromString(userIdStr);

                // Validate User via Feign Client
                verifyUser(userId);

                BrandEntity brand = brandRepository.findById(request.getBrandId())
                                .orElseThrow(() -> new RuntimeException("Brand not found"));

                VehicleEntity vehicle = vehicleMapper.toEntity(request, brand, userId);

                VehicleEntity savedVehicle = vehicleRepository.save(vehicle);

                return vehicleMapper.toResponse(savedVehicle);
        }

        @CircuitBreaker(name = "identity", fallbackMethod = "verifyUserFallback")
        private void verifyUser(java.util.UUID userId) {
                identityClient.getUserById(userId);
        }

        private void verifyUserFallback(java.util.UUID userId, Throwable throwable) {
                throw new RuntimeException("User verification unavailable");
        }
}
