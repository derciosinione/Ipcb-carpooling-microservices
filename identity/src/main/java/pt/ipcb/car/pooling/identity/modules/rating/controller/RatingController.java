package pt.ipcb.car.pooling.identity.modules.rating.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.request.CreateRatingRequest;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.request.UpdateRatingRequest;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.response.RatingResponse;
import pt.ipcb.car.pooling.identity.modules.rating.useCases.CreateRatingUseCase;
import pt.ipcb.car.pooling.identity.modules.rating.useCases.DeleteRatingUseCase;
import pt.ipcb.car.pooling.identity.modules.rating.useCases.GetRatingsForUserUseCase;
import pt.ipcb.car.pooling.identity.modules.rating.useCases.UpdateRatingUseCase;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final CreateRatingUseCase createRatingUseCase;
    private final GetRatingsForUserUseCase getRatingsForUserUseCase;
    private final UpdateRatingUseCase updateRatingUseCase;
    private final DeleteRatingUseCase deleteRatingUseCase;

    @PostMapping
    public ResponseEntity<RatingResponse> createRating(@Valid @RequestBody CreateRatingRequest request) {
        var response = createRatingUseCase.execute(request);
        return ResponseEntity
                .created(URI.create("/ratings/" + response.id()))
                .body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RatingResponse>> getRatingsForUser(@PathVariable UUID userId) {
        var response = getRatingsForUserUseCase.execute(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingResponse> updateRating(@PathVariable UUID id,
            @Valid @RequestBody UpdateRatingRequest request) {
        var response = updateRatingUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable UUID id, @RequestParam UUID raterId) {
        deleteRatingUseCase.execute(id, raterId);
        return ResponseEntity.noContent().build();
    }
}
