package pt.ipcb.carpooling.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pt.ipcb.carpooling.dto.RatingDto;

import java.util.List;

@FeignClient(name = "cloud-gateway", url = "${api.gateway.url}", contextId = "ratingsClient")
public interface RatingsClient {

    @PostMapping("/identity/api/v1/ratings")
    RatingDto.RatingResponse create(@RequestBody RatingDto.CreateRatingRequest request);

    @GetMapping("/identity/api/v1/ratings/user/{userId}")
    List<RatingDto.RatingResponse> getRatingsForUser(@PathVariable("userId") String userId);
}
