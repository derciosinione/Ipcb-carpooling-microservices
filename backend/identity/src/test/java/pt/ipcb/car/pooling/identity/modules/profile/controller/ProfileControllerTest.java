package pt.ipcb.car.pooling.identity.modules.profile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.ipcb.car.pooling.identity.security.SecurityConfig;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.request.CreateProfileRequest;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.response.ProfileResponse;
import pt.ipcb.car.pooling.identity.modules.profile.useCases.CreateProfileUseCase;
import pt.ipcb.car.pooling.identity.modules.profile.useCases.GetAllProfileUseCase;
import pt.ipcb.car.pooling.identity.modules.profile.useCases.GetProfileByIdUseCase;
import pt.ipcb.car.pooling.identity.security.JwtProvider;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@Import({ SecurityConfig.class })
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateProfileUseCase createProfileUseCase;
    @MockitoBean
    private GetAllProfileUseCase getAllProfileUseCase;
    @MockitoBean
    private GetProfileByIdUseCase getProfileByIdUseCase;
    @MockitoBean
    private JwtProvider jwtProvider;

    @Test
    void createProfile_ShouldReturnCreated() throws Exception {
        CreateProfileRequest request = new CreateProfileRequest("Test", "Desc");
        ProfileResponse response = ProfileResponse.builder().id(UUID.randomUUID()).name("Test").build();

        when(createProfileUseCase.execute(any())).thenReturn(response);
        when(jwtProvider.validateToken(any())).thenReturn("user-id");

        mockMvc.perform(post("/profiles")
                .header("Authorization", "Bearer token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test"));
    }
}
