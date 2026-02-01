package pt.ipcb.car.pooling.payments.usecases.createpayment;

import org.springframework.stereotype.Component;
import pt.ipcb.car.pooling.payments.contracts.CreatePaymentRequest;
import pt.ipcb.car.pooling.payments.contracts.PaymentResponse;
import pt.ipcb.car.pooling.payments.entities.PaymentEntity;
import pt.ipcb.car.pooling.payments.enums.PaymentStatus;

@Component
public class CreatePaymentMapper {

    public PaymentEntity toEntity(CreatePaymentRequest request) {
        return PaymentEntity.builder()
                .tripId(request.tripId())
                .passengerId(request.passengerId())
                .amount(request.amount())
                .status(PaymentStatus.PENDING)
                .build();
    }

    public PaymentResponse toResponse(PaymentEntity entity) {
        return new PaymentResponse(
                entity.getId(),
                entity.getTripId(),
                entity.getPassengerId(),
                entity.getAmount(),
                entity.getStatus()
        );
    }
}
