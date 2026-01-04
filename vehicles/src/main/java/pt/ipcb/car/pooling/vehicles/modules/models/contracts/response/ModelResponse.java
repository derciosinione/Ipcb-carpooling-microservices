package pt.ipcb.car.pooling.vehicles.modules.models.contracts.response;

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
public class ModelResponse {
    private UUID id;
    private String name;
    private UUID brandId;
    private String brandName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
