package pt.ipcb.carpooling.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pt.ipcb.carpooling.dto.BookingDto;
import pt.ipcb.carpooling.dto.ExpenseDto;
import pt.ipcb.carpooling.dto.TripDto;

import java.util.List;

@FeignClient(name = "cloud-gateway", url = "${api.gateway.url}", contextId = "tripsClient")
public interface TripsClient {

    @GetMapping("/trips/api/v1/trips/driver/{driverId}")
    List<TripDto.TripResponse> getTripsByDriver(@PathVariable("driverId") String driverId);

    @GetMapping("/trips/api/v1/trips/passenger/{passengerId}")
    List<TripDto.TripResponse> getTripsByPassenger(@PathVariable("passengerId") String passengerId);

    @GetMapping("/trips/api/v1/trips/{tripId}")
    TripDto.TripResponse getTripById(@PathVariable("tripId") String tripId);

    @GetMapping("/trips/api/v1/trips/search")
    List<TripDto.TripResponse> searchTrips(@RequestParam("origin") String origin,
            @RequestParam("destination") String destination,
            @RequestParam("seats") Integer seats);

    @PostMapping("/trips/api/v1/trips")
    TripDto.TripResponse createTrip(@RequestBody TripDto.CreateTripRequest request);

    @GetMapping("/trips/api/v1/bookings/trip/{tripId}")
    List<BookingDto.BookingResponse> getBookingsByTrip(@PathVariable("tripId") String tripId);

    @GetMapping("/trips/api/v1/bookings/passenger/{passengerId}")
    List<BookingDto.BookingResponse> getBookingsByPassenger(@PathVariable("passengerId") String passengerId);

    @PatchMapping("/trips/api/v1/bookings/{bookingId}/accept")
    BookingDto.BookingResponse acceptBooking(@PathVariable("bookingId") String bookingId);

    @PatchMapping("/trips/api/v1/bookings/{bookingId}/reject")
    BookingDto.BookingResponse rejectBooking(@PathVariable("bookingId") String bookingId);

    @PatchMapping("/trips/api/v1/bookings/{bookingId}/cancel")
    BookingDto.BookingResponse cancelBooking(@PathVariable("bookingId") String bookingId);

    @GetMapping("/trips/api/v1/expenses/trip/{tripId}")
    List<ExpenseDto.ExpenseResponse> getExpensesByTrip(@PathVariable("tripId") String tripId);

    @PostMapping("/trips/api/v1/expenses")
    ExpenseDto.ExpenseResponse createExpense(@RequestBody ExpenseDto.CreateExpenseRequest request);
}
