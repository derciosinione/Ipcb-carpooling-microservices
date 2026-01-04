package pt.ipcb.car.pooling.vehicles.modules.vehicles.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

// service name must match identity application name
@FeignClient(name = "car-pooling-identity-api")
public interface IdentityClient {

    @GetMapping("/users/{id}")
    Object getUserById(@PathVariable("id") UUID id);
    // Returning Object for now to avoid checking UserResponse details in Vehicles
    // service
}
