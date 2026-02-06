package pt.ipcb.carpooling.controllers.site;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pt.ipcb.carpooling.clients.IdentityClient;
import pt.ipcb.carpooling.clients.RatingsClient;
import pt.ipcb.carpooling.dto.RatingDto;
import pt.ipcb.carpooling.dto.UserDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RideController {

    private final RatingsClient ratingsClient;
    private final IdentityClient identityClient;

    @GetMapping("/ride/{id}")
    public String rideDetails(@PathVariable Long id, Model model) {
        // In a real app, retrieve ride details by ID
        return "ride-details";
    }

    @GetMapping("/driver/{id}")
    public String driverDetails(@PathVariable String id, Model model) {
        UserDto.PublicUserResponse user = identityClient.getPublicUserById(id);
        List<RatingDto.RatingResponse> ratings = ratingsClient.getRatingsForUser(id);
        BigDecimal avgRating = BigDecimal.ZERO;
        if (!ratings.isEmpty()) {
            int sum = ratings.stream().mapToInt(r -> r.getStars() != null ? r.getStars() : 0).sum();
            avgRating = BigDecimal.valueOf(sum)
                    .divide(BigDecimal.valueOf(ratings.size()), 2, RoundingMode.HALF_UP);
        }

        String displayName = user != null && user.getName() != null ? user.getName() : null;
        if (displayName == null && !ratings.isEmpty()) {
            displayName = ratings.get(0).getRatedUserName();
        }
        if (displayName == null) {
            displayName = "Utilizador";
        }

        model.addAttribute("driver", user);
        model.addAttribute("driverName", displayName);
        model.addAttribute("ratings", ratings);
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("ratingsCount", ratings.size());
        return "driver-details";
    }
}
