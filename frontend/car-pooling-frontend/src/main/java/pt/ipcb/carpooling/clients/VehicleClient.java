package pt.ipcb.carpooling.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pt.ipcb.carpooling.dto.VehicleDto;

import java.util.List;

@FeignClient(name = "cloud-gateway", url = "${api.gateway.url}", contextId = "vehicleClient")
public interface VehicleClient {

    @GetMapping("/vehicles/api/v1/vehicles")
    List<VehicleDto.VehicleResponse> getAllVehicles();

    @GetMapping("/vehicles/api/v1/vehicles/user/{userId}")
    List<VehicleDto.VehicleResponse> getVehiclesByOwner(@PathVariable("userId") String userId);

    @PostMapping("/vehicles/api/v1/vehicles")
    VehicleDto.VehicleResponse createVehicle(@RequestBody VehicleDto.VehicleRequest request);

    @GetMapping("/vehicles/api/v1/brands")
    List<VehicleDto.BrandResponse> getAllBrands();

    @DeleteMapping("/vehicles/api/v1/vehicles/{id}")
    void deleteVehicle(@PathVariable("id") String id);
}
