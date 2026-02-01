package pt.ipcb.carpooling.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pt.ipcb.carpooling.dto.AuthDto;

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
}
