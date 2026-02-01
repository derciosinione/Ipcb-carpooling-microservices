package pt.ipcb.car.pooling.trips.modules.trip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ipcb.car.pooling.trips.modules.entities.ExpenseEntity;
import pt.ipcb.car.pooling.trips.modules.entities.TripEntity;
import pt.ipcb.car.pooling.trips.modules.repositories.ExpenseRepository;
import pt.ipcb.car.pooling.trips.modules.repositories.TripRepository;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.CreateExpenseRequest;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.ExpenseResponse;
import pt.ipcb.car.pooling.trips.modules.trip.mapper.ExpenseMapper;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;
    private final ExpenseMapper expenseMapper;

    @Transactional
    public ExpenseResponse createExpense(CreateExpenseRequest request){

        TripEntity trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + request.getTripId()));

        ExpenseEntity expense = expenseMapper.toEntity(request);

        expense.setTrip(trip);

        BigDecimal newTotal = trip.getTotalCost().add(request.getAmount());

        trip.setTotalCost(newTotal);

        tripRepository.save(trip);

        ExpenseEntity savedExpense = expenseRepository.save(expense);

        return expenseMapper.toResponse(savedExpense);
    }

}
