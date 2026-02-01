package pt.ipcb.car.pooling.trips.modules.trip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ipcb.car.pooling.trips.exceptions.ForbiddenException;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.CreateExpenseRequest;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.ExpenseResponse;
import pt.ipcb.car.pooling.trips.modules.trip.service.ExpenseService;
import pt.ipcb.car.pooling.trips.modules.trip.service.TripService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final TripService tripService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody CreateExpenseRequest request,
            HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        UUID driverId = tripService.getTripById(request.getTripId()).getDriverId();
        if (!driverId.equals(userId)) {
            throw new ForbiddenException("Only the driver can register expenses");
        }
        ExpenseResponse response = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<ExpenseResponse>> listByTrip(@PathVariable UUID tripId) {
        return ResponseEntity.ok(expenseService.listExpensesByTrip(tripId));
    }

    private UUID requireUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new ForbiddenException("Unauthorized");
        }
        return UUID.fromString(userId.toString());
    }

}
