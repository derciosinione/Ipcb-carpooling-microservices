package pt.ipcb.carpooling.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pt.ipcb.carpooling.dto.LocationDto;

import java.util.List;

@FeignClient(name = "cloud-gateway", url = "${api.gateway.url}", contextId = "gpsClient")
public interface GpsClient {

    @GetMapping("/gps/api/v1/locations/search")
    List<LocationDto.LocationSuggestionResponse> searchLocations(@RequestParam("q") String query,
            @RequestParam("limit") Integer limit);

    @PostMapping("/gps/api/v1/locations/users/{userId}/recent")
    LocationDto.LocationSuggestionResponse saveRecentLocation(@PathVariable("userId") String userId,
            @RequestBody LocationDto.SaveUserLocationRequest request);
}
