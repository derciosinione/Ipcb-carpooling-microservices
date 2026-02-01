package pt.ipcb.car.pooling.trips.modules.trip.mapper;

import org.springframework.stereotype.Component;
import pt.ipcb.car.pooling.trips.modules.entities.TripEntity;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.CreateTripRequest;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.TripResponse;

@Component
public class TripMapper {

    public TripEntity toEntity(CreateTripRequest request){
        TripEntity entity = new TripEntity();
        entity.setDriverId(request.getDriverId());
        entity.setVehicleId(request.getVehicleId());
        entity.setOrigin(request.getOrigin());
        entity.setDestination(request.getDestination());
        entity.setDescription(request.getDescription());
        entity.setDepartureTime(request.getDepartureTime());
        entity.setAvailableSeats(request.getAvailableSeats());
        if (request.getDistanceKm() != null) {
            entity.setDistanceKm(request.getDistanceKm());
        }
        return entity;
    }

    public TripResponse toResponse(TripEntity entity){
        TripResponse response = new TripResponse();
        response.setId(entity.getId());
        response.setDriverId(entity.getDriverId());
        response.setVehicleId(entity.getVehicleId());
        response.setOrigin(entity.getOrigin());
        response.setDestination(entity.getDestination());
        response.setDescription(entity.getDescription());
        response.setDepartureTime(entity.getDepartureTime());
        response.setAvailableSeats(entity.getAvailableSeats());
        response.setTotalCost(entity.getTotalCost());
        response.setDistanceKm(entity.getDistanceKm());
        response.setCreatedAt(entity.getCreatedAt());

        if (entity.getStatus() != null){
            response.setStatus(entity.getStatus().getName());
        }

        return response;
    }

}
