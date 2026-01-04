package pt.ipcb.car.pooling.vehicles.modules.vehicles.contracts.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponse {
    private UUID id;
    private UUID modelId;
    private String modelName;
    private String brandName;
    private String licensePlate;
    private Integer year;
    private String color;
    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
