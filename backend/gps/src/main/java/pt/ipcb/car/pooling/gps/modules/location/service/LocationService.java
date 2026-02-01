package pt.ipcb.car.pooling.gps.modules.location.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pt.ipcb.car.pooling.gps.modules.location.contracts.LocationSuggestionResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${gps.nominatim.base-url:https://nominatim.openstreetmap.org}")
    private String nominatimBaseUrl;

    @Value("${gps.nominatim.user-agent:IPCB-CarPooling/1.0}")
    private String userAgent;

    public List<LocationSuggestionResponse> search(String query, int limit) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String body = restClient.get()
                    .uri(nominatimBaseUrl + "/search?format=jsonv2&addressdetails=1&limit=" + limit + "&q="
                            + encodedQuery)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("User-Agent", userAgent)
                    .retrieve()
                    .body(String.class);

            if (body == null || body.isBlank()) {
                return List.of();
            }

            JsonNode nodes = objectMapper.readTree(body);
            List<LocationSuggestionResponse> result = new ArrayList<>();
            for (JsonNode node : nodes) {
                String name = node.path("display_name").asText();
                Double lat = node.path("lat").asDouble();
                Double lon = node.path("lon").asDouble();
                if (!name.isBlank()) {
                    result.add(new LocationSuggestionResponse(name, lat, lon));
                }
            }
            return result;
        } catch (Exception e) {
            log.warn("Error searching locations in Nominatim: {}", e.getMessage());
            return List.of();
        }
    }

    public LocationSuggestionResponse reverse(Double lat, Double lon) {
        try {
            String body = restClient.get()
                    .uri(nominatimBaseUrl + "/reverse?format=jsonv2&lat=" + lat + "&lon=" + lon)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("User-Agent", userAgent)
                    .retrieve()
                    .body(String.class);

            if (body == null || body.isBlank()) {
                return null;
            }

            JsonNode node = objectMapper.readTree(body);
            String name = node.path("display_name").asText("");
            if (name.isBlank()) {
                return null;
            }
            return new LocationSuggestionResponse(name, lat, lon);
        } catch (Exception e) {
            log.warn("Error reverse geocoding in Nominatim: {}", e.getMessage());
            return null;
        }
    }

    public BigDecimal calculateDistanceKm(Double originLat, Double originLon, Double destinationLat, Double destinationLon) {
        double dLat = Math.toRadians(destinationLat - originLat);
        double dLon = Math.toRadians(destinationLon - originLon);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(originLat)) * Math.cos(Math.toRadians(destinationLat))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS_KM * c;

        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
    }
}
