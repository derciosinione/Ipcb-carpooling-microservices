package pt.ipcb.car.pooling.trips.modules.trip.mapper;

import org.springframework.stereotype.Component;
import pt.ipcb.car.pooling.trips.modules.entities.BookingEntity;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.BookingResponse;

@Component
public class BookingMapper {

    public BookingResponse toResponse(BookingEntity entity){
        BookingResponse response = new BookingResponse();
        response.setId(entity.getId());
        response.setPassengerId(entity.getPassengerId());
        response.setSeats(entity.getSeats());
        response.setPriceToPay(entity.getPriceToPay());
        response.setPaid(Boolean.TRUE.equals(entity.getPaid()));
        response.setPaidAt(entity.getPaidAt());
        response.setPaymentReference(entity.getPaymentReference());
        response.setCreatedAt(entity.getCreatedAt());


        if (entity.getTrip() != null){
            response.setTripId(entity.getTrip().getId());
        }

        if (entity.getStatus() != null){
            response.setStatus(entity.getStatus().getName());
        }

        return response;
    }
}
