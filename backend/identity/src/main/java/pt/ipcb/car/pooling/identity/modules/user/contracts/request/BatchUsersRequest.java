package pt.ipcb.car.pooling.identity.modules.user.contracts.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BatchUsersRequest {
    @NotEmpty
    private List<UUID> ids;
}
