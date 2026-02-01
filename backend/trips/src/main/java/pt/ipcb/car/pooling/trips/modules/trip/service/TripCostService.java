package pt.ipcb.car.pooling.trips.modules.trip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ipcb.car.pooling.trips.exceptions.NotFoundException;
import pt.ipcb.car.pooling.trips.modules.entities.BookingEntity;
import pt.ipcb.car.pooling.trips.modules.entities.TripEntity;
import pt.ipcb.car.pooling.trips.modules.repositories.BookingRepository;
import pt.ipcb.car.pooling.trips.modules.repositories.TripRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripCostService {

    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public void recalculateCosts(UUID tripId) {
        TripEntity trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with id: " + tripId));

        Integer confirmedSeats = bookingRepository.sumConfirmedSeatsByTripId(tripId);
        int totalTravelers = (confirmedSeats != null ? confirmedSeats : 0) + 1;

        BigDecimal totalCost = trip.getTotalCost() == null ? BigDecimal.ZERO : trip.getTotalCost();
        BigDecimal costPerTraveler = BigDecimal.ZERO;
        costPerTraveler = totalCost
                .divide(BigDecimal.valueOf(totalTravelers), 2, RoundingMode.HALF_UP);

        List<BookingEntity> bookings = bookingRepository.findByTripId(tripId);
        for (BookingEntity booking : bookings) {
            if ("CONFIRMED".equals(booking.getStatus().getName())) {
                BigDecimal seatCost = costPerTraveler.multiply(BigDecimal.valueOf(booking.getSeats()));
                booking.setPriceToPay(seatCost);
            } else {
                booking.setPriceToPay(BigDecimal.ZERO);
            }
        }
        bookingRepository.saveAll(bookings);
    }
}
