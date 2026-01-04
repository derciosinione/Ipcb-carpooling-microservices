package pt.ipcb.car.pooling.identity.modules.user.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ipcb.car.pooling.identity.modules.user.contracts.request.UpdateUserRequest;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.UserResponse;
import pt.ipcb.car.pooling.identity.modules.user.useCases.AddProfileToUserUseCase;
import pt.ipcb.car.pooling.identity.modules.user.useCases.GetAllUsersUseCase;
import pt.ipcb.car.pooling.identity.modules.user.useCases.GetUserByIdUseCase;
import pt.ipcb.car.pooling.identity.modules.user.useCases.RemoveProfileFromUserUseCase;
import pt.ipcb.car.pooling.identity.modules.user.useCases.UpdateUserUseCase;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
// @SecurityRequirement(name = "jwt_auth")
public class UserController {

    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final AddProfileToUserUseCase addProfileToUserUseCase;
    private final RemoveProfileFromUserUseCase removeProfileFromUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    @GetMapping("")
    public ResponseEntity<List<UserResponse>> getAllUser() {
        var data = getAllUsersUseCase.execute();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        var data = getUserByIdUseCase.execute(id);
        return ResponseEntity.ok(data);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        var data = updateUserUseCase.execute(id, request);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/{id}/profiles/{profileName}")
    public ResponseEntity<UserResponse> addProfile(@PathVariable UUID id, @PathVariable String profileName) {
        var data = addProfileToUserUseCase.execute(id, profileName);
        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/{id}/profiles/{profileName}")
    public ResponseEntity<UserResponse> removeProfile(@PathVariable UUID id, @PathVariable String profileName) {
        var data = removeProfileFromUserUseCase.execute(id, profileName);
        return ResponseEntity.ok(data);
    }
}
