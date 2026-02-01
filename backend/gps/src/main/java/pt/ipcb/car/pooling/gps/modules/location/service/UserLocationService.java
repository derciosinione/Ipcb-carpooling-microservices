package pt.ipcb.car.pooling.gps.modules.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ipcb.car.pooling.gps.modules.entities.UserLocationEntity;
import pt.ipcb.car.pooling.gps.modules.location.contracts.LocationSuggestionResponse;
import pt.ipcb.car.pooling.gps.modules.location.contracts.SaveUserLocationRequest;
import pt.ipcb.car.pooling.gps.modules.repositories.UserLocationRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserLocationService {

    private final UserLocationRepository userLocationRepository;

    @Transactional
    public LocationSuggestionResponse saveRecent(UUID userId, SaveUserLocationRequest request) {
        UserLocationEntity entity = userLocationRepository.findByUserIdAndLabelIgnoreCase(userId, request.getLabel())
                .orElseGet(UserLocationEntity::new);

        entity.setUserId(userId);
        entity.setLabel(request.getLabel());
        entity.setLat(request.getLat());
        entity.setLon(request.getLon());

        UserLocationEntity saved = userLocationRepository.save(entity);
        return new LocationSuggestionResponse(saved.getLabel(), saved.getLat(), saved.getLon());
    }

    public List<LocationSuggestionResponse> listRecent(UUID userId) {
        return userLocationRepository.findTop10ByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(item -> new LocationSuggestionResponse(item.getLabel(), item.getLat(), item.getLon()))
                .toList();
    }
}
