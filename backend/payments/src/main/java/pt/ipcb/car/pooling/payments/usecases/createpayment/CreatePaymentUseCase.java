package pt.ipcb.car.pooling.payments.usecases.createpayment;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.payments.contracts.CreatePaymentRequest;
import pt.ipcb.car.pooling.payments.contracts.PaymentResponse;
import pt.ipcb.car.pooling.payments.entities.PaymentEntity;
import pt.ipcb.car.pooling.payments.repositories.PaymentRepository;

@Service
@RequiredArgsConstructor
public class CreatePaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final CreatePaymentMapper createPaymentMapper;

    public PaymentResponse execute(CreatePaymentRequest request) {
        PaymentEntity entity = createPaymentMapper.toEntity(request);
        PaymentEntity saved = paymentRepository.save(entity);
        return createPaymentMapper.toResponse(saved);
    }

}
