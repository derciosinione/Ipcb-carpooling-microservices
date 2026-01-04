package pt.ipcb.car.pooling.vehicles.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ValidationErrorResponse {
    private LocalDateTime timestamp;
    private String field;
    private String message;
}
