package pt.ipcb.car.pooling.identity.modules.profile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.request.CreateProfileRequest;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.response.ProfileResponse;
import pt.ipcb.car.pooling.identity.modules.profile.useCases.CreateProfileUseCase;
import pt.ipcb.car.pooling.identity.modules.profile.useCases.GetAllProfileUseCase;
import pt.ipcb.car.pooling.identity.modules.profile.useCases.GetProfileByIdUseCase;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final CreateProfileUseCase createProfileUseCase;
    private final GetAllProfileUseCase getAllProfileUseCase;
    private final GetProfileByIdUseCase getProfileByIdUseCase;

    @GetMapping("")
    public ResponseEntity<List<ProfileResponse>> getAll() {
        var data = getAllProfileUseCase.execute();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getById(@PathVariable UUID id) {
        var data = getProfileByIdUseCase.execute(id);
        return ResponseEntity.ok(data);
    }

    @PostMapping("")
    public ResponseEntity<ProfileResponse> create(@Valid @RequestBody CreateProfileRequest request) {

        var response = createProfileUseCase.execute(request);

        return ResponseEntity
                .created(URI.create("/profiles/" + response.id()))
                .body(response);
    }
}
