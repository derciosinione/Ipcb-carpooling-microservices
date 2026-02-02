package pt.ipcb.car.pooling.identity.modules.rating.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ipcb.car.pooling.identity.exceptions.ActionNotAllowedException;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.request.CreateRatingRequest;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.request.UpdateRatingRequest;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.response.RatingResponse;
import pt.ipcb.car.pooling.identity.modules.rating.useCases.CreateRatingUseCase;
import pt.ipcb.car.pooling.identity.modules.rating.useCases.DeleteRatingUseCase;
import pt.ipcb.car.pooling.identity.modules.rating.useCases.GetRatingsForUserUseCase;
import pt.ipcb.car.pooling.identity.modules.rating.useCases.UpdateRatingUseCase;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final CreateRatingUseCase createRatingUseCase;
    private final GetRatingsForUserUseCase getRatingsForUserUseCase;
    private final UpdateRatingUseCase updateRatingUseCase;
    private final DeleteRatingUseCase deleteRatingUseCase;

    @PostMapping
    public ResponseEntity<RatingResponse> createRating(@Valid @RequestBody CreateRatingRequest request,
            HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        if (!userId.equals(request.raterId())) {
            throw new ActionNotAllowedException("User not allowed to rate as another user");
        }
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
            @Valid @RequestBody UpdateRatingRequest request,
            HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        if (!userId.equals(request.raterId())) {
            throw new ActionNotAllowedException("User not allowed to update rating as another user");
        }
        var response = updateRatingUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable UUID id,
            @RequestParam(required = false) UUID raterId,
            HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        if (raterId != null && !raterId.equals(userId)) {
            throw new ActionNotAllowedException("User not allowed to delete rating as another user");
        }
        deleteRatingUseCase.execute(id, userId);
        return ResponseEntity.noContent().build();
    }

    private UUID requireUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new ActionNotAllowedException("Unauthorized");
        }
        return UUID.fromString(userId.toString());
    }
}
