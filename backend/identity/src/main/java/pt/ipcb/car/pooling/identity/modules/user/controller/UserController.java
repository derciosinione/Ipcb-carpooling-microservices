package pt.ipcb.car.pooling.identity.modules.user.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.RestController;
import pt.ipcb.car.pooling.identity.exceptions.ActionNotAllowedException;
import pt.ipcb.car.pooling.identity.modules.user.contracts.request.UpdateUserRequest;
import pt.ipcb.car.pooling.identity.modules.user.contracts.request.BatchUsersRequest;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.PublicUserResponse;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.UserResponse;
import pt.ipcb.car.pooling.identity.modules.user.useCases.AddProfileToUserUseCase;
import pt.ipcb.car.pooling.identity.modules.user.useCases.GetAllUsersUseCase;
import pt.ipcb.car.pooling.identity.modules.user.useCases.GetPublicUserByIdUseCase;
import pt.ipcb.car.pooling.identity.modules.user.useCases.GetUserByIdUseCase;
import pt.ipcb.car.pooling.identity.modules.user.useCases.GetUsersByIdsUseCase;
import pt.ipcb.car.pooling.identity.modules.user.useCases.RemoveProfileFromUserUseCase;
import pt.ipcb.car.pooling.identity.modules.user.useCases.UpdateUserUseCase;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
// @SecurityRequirement(name = "jwt_auth")
public class UserController {

    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final GetPublicUserByIdUseCase getPublicUserByIdUseCase;
    private final GetUsersByIdsUseCase getUsersByIdsUseCase;
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

    @GetMapping("/{id}/public")
    public ResponseEntity<PublicUserResponse> getPublicUserById(@PathVariable UUID id) {
        var data = getPublicUserByIdUseCase.execute(id);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<UserResponse>> getUsersByIds(@Valid @RequestBody BatchUsersRequest request) {
        var data = getUsersByIdsUseCase.execute(request.getIds());
        return ResponseEntity.ok(data);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request,
            HttpServletRequest httpRequest) {
        ensureAdminOrSelf(httpRequest, id);
        var data = updateUserUseCase.execute(id, request);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/{id}/profiles/{profileName}")
    public ResponseEntity<UserResponse> addProfile(@PathVariable UUID id,
            @PathVariable String profileName,
            HttpServletRequest httpRequest) {
        ensureAdminOrSelf(httpRequest, id);
        var data = addProfileToUserUseCase.execute(id, profileName);
        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/{id}/profiles/{profileName}")
    public ResponseEntity<UserResponse> removeProfile(@PathVariable UUID id,
            @PathVariable String profileName,
            HttpServletRequest httpRequest) {
        ensureAdminOrSelf(httpRequest, id);
        var data = removeProfileFromUserUseCase.execute(id, profileName);
        return ResponseEntity.ok(data);
    }

    private void ensureAdminOrSelf(HttpServletRequest request, UUID targetUserId) {
        UUID userId = requireUserId(request);
        if (userId.equals(targetUserId)) {
            return;
        }
        if (!hasAdminRole(request)) {
            throw new ActionNotAllowedException("Action not allowed");
        }
    }

    private UUID requireUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new ActionNotAllowedException("Unauthorized");
        }
        return UUID.fromString(userId.toString());
    }

    private boolean hasAdminRole(HttpServletRequest request) {
        Object rolesObj = request.getAttribute("roles");
        if (rolesObj == null) {
            return false;
        }
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) rolesObj;
        return roles.stream().anyMatch(r -> r != null && r.equalsIgnoreCase("Admin"));
    }
}
