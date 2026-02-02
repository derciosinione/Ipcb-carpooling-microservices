package pt.ipcb.carpooling.services.identity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.carpooling.clients.IdentityClient;
import pt.ipcb.carpooling.dto.UserDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IdentityDashboardService {

    private final IdentityClient identityClient;

    public Map<String, UserDto.UserResponse> fetchUsersByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }
        UserDto.BatchUsersRequest request = new UserDto.BatchUsersRequest(ids);
        return identityClient.getUsersByIds(request).stream()
                .collect(Collectors.toMap(UserDto.UserResponse::getId, u -> u));
    }

    public String safeName(UserDto.UserResponse user) {
        if (user == null) {
            return "Utilizador";
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            return user.getName();
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            return user.getEmail();
        }
        return "Utilizador";
    }

    public String initials(UserDto.UserResponse user) {
        String name = safeName(user);
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 0) {
            return "U";
        }
        String first = parts[0];
        String last = parts.length > 1 ? parts[parts.length - 1] : "";
        String init = "";
        if (!first.isEmpty()) {
            init += first.charAt(0);
        }
        if (!last.isEmpty()) {
            init += last.charAt(0);
        }
        return init.isEmpty() ? "U" : init.toUpperCase();
    }
}
