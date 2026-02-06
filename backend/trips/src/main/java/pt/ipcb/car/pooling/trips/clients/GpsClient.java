package pt.ipcb.car.pooling.trips.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.gps.DistanceRequest;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.gps.DistanceResponse;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.gps.LocationSuggestionResponse;

@FeignClient(name = "car-pooling-gps-api", contextId = "gpsClient")
public interface GpsClient {

    @GetMapping("/api/v1/locations/reverse")
    LocationSuggestionResponse reverse(@RequestParam("lat") Double lat, @RequestParam("lon") Double lon);

    @PostMapping("/api/v1/locations/distance")
    DistanceResponse distance(@RequestBody DistanceRequest request);
}
