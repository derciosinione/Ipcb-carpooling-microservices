package pt.ipcb.car.pooling.identity.modules.rating.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.ipcb.car.pooling.identity.security.SecurityConfig;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.request.CreateRatingRequest;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.response.RatingResponse;
import pt.ipcb.car.pooling.identity.modules.rating.useCases.CreateRatingUseCase;
import pt.ipcb.car.pooling.identity.modules.rating.useCases.DeleteRatingUseCase;
import pt.ipcb.car.pooling.identity.modules.rating.useCases.GetRatingsForUserUseCase;
import pt.ipcb.car.pooling.identity.modules.rating.useCases.UpdateRatingUseCase;
import pt.ipcb.car.pooling.identity.security.JwtProvider;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RatingController.class)
@Import({ SecurityConfig.class })
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateRatingUseCase createRatingUseCase;

    @MockitoBean
    private GetRatingsForUserUseCase getRatingsForUserUseCase;

    @MockitoBean
    private UpdateRatingUseCase updateRatingUseCase;

    @MockitoBean
    private DeleteRatingUseCase deleteRatingUseCase;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Test
    void createRating_ShouldReturnCreated() throws Exception {
        UUID id = UUID.randomUUID();
        CreateRatingRequest request = new CreateRatingRequest(id, id, 5, "Good");
        RatingResponse response = RatingResponse.builder().id(id).stars(5).build();

        when(createRatingUseCase.execute(any())).thenReturn(response);
        when(jwtProvider.validateToken(any())).thenReturn("user-id");

        mockMvc.perform(post("/ratings")
                .header("Authorization", "Bearer token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stars").value(5));
    }
}
