package pt.ipcb.car.pooling.identity.modules.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.ipcb.car.pooling.identity.modules.user.contracts.request.AuthRequest;
import pt.ipcb.car.pooling.identity.modules.user.contracts.request.RegisterUserRequest;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.AuthResponse;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.UserResponse;
import pt.ipcb.car.pooling.identity.modules.user.useCases.AuthUseCase;
import pt.ipcb.car.pooling.identity.modules.user.useCases.CreateUserUseCase;
import pt.ipcb.car.pooling.identity.utils.ProfileConstants;
import pt.ipcb.car.pooling.identity.security.SecurityConfig;
import pt.ipcb.car.pooling.identity.security.JwtProvider;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({ SecurityConfig.class })
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthUseCase authUseCase;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @MockitoBean // Use JwtProvider instead of TokenService
    private JwtProvider jwtProvider;

    @Test
    void auth_ShouldReturnToken_WhenCredentialsValid() throws Exception {
        AuthRequest request = new AuthRequest("me@test.com", "pass");
        AuthResponse response = new AuthResponse("me@test.com", "token123");

        when(authUseCase.execute(request)).thenReturn(response);

        mockMvc.perform(post("/auth/sign-in")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"));
    }

    @Test
    void registerPassenger_ShouldCreatePassenger() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("pass")
                .email("pass@test.com")
                .password("password123")
                .build();
        UserResponse response = UserResponse.builder().id(UUID.randomUUID()).build();

        when(createUserUseCase.execute(any(), eq(Set.of(ProfileConstants.PASSENGER)))).thenReturn(response);

        mockMvc.perform(post("/auth/register/passenger")
                .with(csrf()) // Must include csrf for POST
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
