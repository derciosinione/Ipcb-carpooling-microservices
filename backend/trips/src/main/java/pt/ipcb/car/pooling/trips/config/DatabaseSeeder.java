package pt.ipcb.car.pooling.trips.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pt.ipcb.car.pooling.trips.modules.entities.BookingStatusEntity;
import pt.ipcb.car.pooling.trips.modules.entities.TripStatusEntity;
import pt.ipcb.car.pooling.trips.modules.repositories.BookingStatusRepository;
import pt.ipcb.car.pooling.trips.modules.repositories.TripStatusRepository;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {
    private final TripStatusRepository tripStatusRepository;
    private final BookingStatusRepository bookingStatusRepository;

    @Override
    public void run(String... args) throws Exception {
        seedTripStatuses();
        seedBookingStatuses();
    }

    private void seedTripStatuses() {
        Arrays.asList("OPEN", "FULL", "STARTED", "FINISHED", "CANCELED").forEach(statusName -> {
            if (tripStatusRepository.findByName(statusName).isEmpty()) {
                TripStatusEntity entity = new TripStatusEntity();
                entity.setName(statusName);
                entity.setDescription("Status generated automatically");
                tripStatusRepository.save(entity);
                System.out.println(" Trip Status created: " + statusName);
            }
        });
    }

    private void seedBookingStatuses() {
        Arrays.asList("PENDING", "CONFIRMED", "REJECTED", "CANCELED").forEach(statusName -> {
            if (bookingStatusRepository.findByName(statusName).isEmpty()) {
                BookingStatusEntity entity = new BookingStatusEntity();
                entity.setName(statusName);
                bookingStatusRepository.save(entity);
                System.out.println("Booking Status created: " + statusName);
            }
        });
    }
}
