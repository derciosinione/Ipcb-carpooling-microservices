package pt.ipcb.car.pooling.cloudgatewayservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/identity")
    public Mono<ResponseEntity<String>> identityFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Identity Service is currently unavailable. Please try again later."));
    }

    @RequestMapping("/vehicles")
    public Mono<ResponseEntity<String>> vehiclesFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Vehicles Service is currently unavailable. Please try again later."));
    }
}
