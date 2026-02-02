package pt.ipcb.car.pooling.identity.modules.admin.contracts.request;

import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(@NotNull Boolean active) {
}
