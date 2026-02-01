package pt.ipcb.car.pooling.identity.exceptions;

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
