package pt.ipcb.car.pooling.identity.modules.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ipcb.car.pooling.identity.exceptions.ActionNotAllowedException;
import pt.ipcb.car.pooling.identity.modules.admin.contracts.request.UpdateUserStatusRequest;
import pt.ipcb.car.pooling.identity.modules.admin.useCases.UpdateUserStatusUseCase;
import pt.ipcb.car.pooling.identity.modules.user.contracts.request.RegisterUserRequest;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.UserResponse;
import pt.ipcb.car.pooling.identity.modules.user.repository.IUserRepository;
import pt.ipcb.car.pooling.identity.modules.user.useCases.CreateUserUseCase;
import pt.ipcb.car.pooling.identity.modules.user.useCases.GetAllUsersUseCase;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CreateUserUseCase createUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final UpdateUserStatusUseCase updateUserStatusUseCase;
    private final IUserRepository userRepository;

    @PostMapping("/bootstrap")
    public ResponseEntity<UserResponse> bootstrapAdmin(@Valid @RequestBody RegisterUserRequest request) {
        if (userRepository.existsByProfiles_NameIgnoreCase("Admin")) {
            throw new ActionNotAllowedException("Admin already exists");
        }
        var response = createUserUseCase.execute(request, Set.of("Admin"));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createAdmin(@Valid @RequestBody RegisterUserRequest request,
            HttpServletRequest httpRequest) {
        requireAdmin(httpRequest);
        var response = createUserUseCase.execute(request, Set.of("Admin"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> listUsers(HttpServletRequest httpRequest) {
        requireAdmin(httpRequest);
        return ResponseEntity.ok(getAllUsersUseCase.execute());
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<UserResponse> updateStatus(@PathVariable UUID id,
            @Valid @RequestBody UpdateUserStatusRequest request,
            HttpServletRequest httpRequest) {
        requireAdmin(httpRequest);
        return ResponseEntity.ok(updateUserStatusUseCase.execute(id, request.active()));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<UserResponse> updateStatusPut(@PathVariable UUID id,
            @Valid @RequestBody UpdateUserStatusRequest request,
            HttpServletRequest httpRequest) {
        requireAdmin(httpRequest);
        return ResponseEntity.ok(updateUserStatusUseCase.execute(id, request.active()));
    }

    private void requireAdmin(HttpServletRequest request) {
        Object rolesObj = request.getAttribute("roles");
        if (rolesObj == null) {
            throw new ActionNotAllowedException("Unauthorized");
        }
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) rolesObj;
        boolean isAdmin = roles.stream().anyMatch(r -> r != null && r.equalsIgnoreCase("Admin"));
        if (!isAdmin) {
            throw new ActionNotAllowedException("Admin privileges required");
        }
    }
}
