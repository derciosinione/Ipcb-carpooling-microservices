package pt.ipcb.car.pooling.payments.usecases.createpayment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pt.ipcb.car.pooling.payments.entities.PaymentEntity;
import pt.ipcb.car.pooling.payments.repositories.PaymentRepository;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetPaymentsByTripUseCase {

    private final PaymentRepository paymentRepository;

    public List<PaymentEntity> execute(UUID tripId){
        return paymentRepository.findByTripId(tripId);
    }
}
