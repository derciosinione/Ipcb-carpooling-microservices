package pt.ipcb.carpooling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerTripDto {
    private TripDto.TripResponse trip;
    private BookingDto.BookingResponse booking;
}
