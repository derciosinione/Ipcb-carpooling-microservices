package pt.ipcb.car.pooling.payments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pt.ipcb.car.pooling.payments.contracts.CreatePaymentRequest;
import pt.ipcb.car.pooling.payments.contracts.PaymentResponse;
import pt.ipcb.car.pooling.payments.entities.PaymentEntity;
import pt.ipcb.car.pooling.payments.usecases.createpayment.CreatePaymentUseCase;
import pt.ipcb.car.pooling.payments.usecases.createpayment.GetPaymentsByTripUseCase;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final CreatePaymentUseCase createPaymentUseCase;
    private final GetPaymentsByTripUseCase getPaymentsByTripUseCase;

    @GetMapping("ping")
    public String ping() {
        return "payments ok!";
    }

    @GetMapping("/trips/{tripId}")
    public List<PaymentEntity> getPaymentsByTripId(@PathVariable UUID tripId) {
        return getPaymentsByTripUseCase.execute(tripId);
    }

    @PostMapping
    public PaymentResponse createPayment(@RequestBody CreatePaymentRequest request) {
        return createPaymentUseCase.execute(request);
    }
}
