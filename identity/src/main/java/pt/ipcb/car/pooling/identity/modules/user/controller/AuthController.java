package pt.ipcb.car.pooling.identity.modules.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ipcb.car.pooling.identity.modules.user.contracts.request.AuthRequest;
import pt.ipcb.car.pooling.identity.modules.user.contracts.request.RegisterUserRequest;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.AuthResponse;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.UserResponse;
import pt.ipcb.car.pooling.identity.modules.user.useCases.AuthUseCase;
import pt.ipcb.car.pooling.identity.modules.user.useCases.CreateUserUseCase;
import pt.ipcb.car.pooling.identity.utils.ProfileConstants;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication Endpoints")
public class AuthController {

    private final AuthUseCase authUseCase;
    private final CreateUserUseCase createUserUseCase;

    @PostMapping("/sign-in")
    @Operation(summary = "Authenticate user", description = "Validates the user's email and password and returns a JWT token if the credentials are correct.")
    public ResponseEntity<AuthResponse> auth(@Valid @RequestBody AuthRequest request) {
        var response = authUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/passenger")
    public ResponseEntity<UserResponse> registerPassenger(@Valid @RequestBody RegisterUserRequest request) {
        var response = createUserUseCase.execute(request, java.util.Set.of(ProfileConstants.PASSENGER));
        return ResponseEntity
                .created(URI.create("/users/" + response.id()))
                .body(response);
    }

    @PostMapping("/register/driver")
    public ResponseEntity<UserResponse> registerDriver(@Valid @RequestBody RegisterUserRequest request) {
        var response = createUserUseCase.execute(request, java.util.Set.of(ProfileConstants.DRIVER));
        return ResponseEntity
                .created(URI.create("/users/" + response.id()))
                .body(response);
    }

    @PostMapping("/register/both")
    public ResponseEntity<UserResponse> registerBoth(@Valid @RequestBody RegisterUserRequest request) {
        var response = createUserUseCase.execute(request,
                java.util.Set.of(ProfileConstants.PASSENGER, ProfileConstants.DRIVER));
        return ResponseEntity
                .created(URI.create("/users/" + response.id()))
                .body(response);
    }
}
