package pt.ipcb.car.pooling.payments.usecases.createpayment;

import org.springframework.stereotype.Component;
import pt.ipcb.car.pooling.payments.contracts.PaymentResponse;
import pt.ipcb.car.pooling.payments.entities.PaymentEntity;

@Component
public class GetPaymentsByTripMapper {

    public PaymentResponse toResponse(PaymentEntity entity){

        return PaymentResponse.builder()
                .id(entity.getId())
                .tripId(entity.getTripId())
                .passengerId(entity.getPassengerId())
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .build();

    }
}
