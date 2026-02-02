package pt.ipcb.carpooling.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.UserDto;

import java.util.List;

@FeignClient(name = "cloud-gateway", url = "${api.gateway.url}", contextId = "identityClient")
public interface IdentityClient {

    @PostMapping("/identity/api/v1/auth/sign-in")
    AuthDto.LoginResponse signIn(@RequestBody AuthDto.LoginRequest request);

    @PostMapping("/identity/api/v1/auth/register/passenger")
    AuthDto.UserResponse registerPassenger(@RequestBody AuthDto.RegisterRequest request);

    @PostMapping("/identity/api/v1/auth/register/driver")
    AuthDto.UserResponse registerDriver(@RequestBody AuthDto.RegisterRequest request);

    @PostMapping("/identity/api/v1/auth/register/both")
    AuthDto.UserResponse registerBoth(@RequestBody AuthDto.RegisterRequest request);

    @PostMapping("/identity/api/v1/users/batch")
    List<UserDto.UserResponse> getUsersByIds(@RequestBody UserDto.BatchUsersRequest request);

    @GetMapping("/identity/api/v1/users/{id}")
    UserDto.UserResponse getUserById(@PathVariable("id") String id);

    @PutMapping("/identity/api/v1/users/{id}")
    UserDto.UserResponse updateUser(@PathVariable("id") String id, @RequestBody UserDto.UpdateUserRequest request);

    @PostMapping("/identity/api/v1/users/{id}/profiles/{profileName}")
    void addProfileToUser(@PathVariable("id") String id, @PathVariable("profileName") String profileName);

    @DeleteMapping("/identity/api/v1/users/{id}/profiles/{profileName}")
    void removeProfileFromUser(@PathVariable("id") String id, @PathVariable("profileName") String profileName);
}
