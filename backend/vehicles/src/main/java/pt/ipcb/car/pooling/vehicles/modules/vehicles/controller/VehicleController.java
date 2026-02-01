package pt.ipcb.car.pooling.vehicles.modules.vehicles.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.contracts.request.CreateVehicleRequest;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.contracts.response.VehicleResponse;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.useCases.CreateVehicleUseCase;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.useCases.ListVehiclesUseCase;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.useCases.ListVehiclesByUserUseCase;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Endpoints for managing vehicles")
public class VehicleController {

    private final CreateVehicleUseCase createVehicleUseCase;
    private final ListVehiclesUseCase listVehiclesUseCase;
    private final ListVehiclesByUserUseCase listVehiclesByUserUseCase;

    @PostMapping
    @Operation(summary = "Create a new vehicle", description = "Creates a new vehicle linked to a model")
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody CreateVehicleRequest request) {
        return ResponseEntity.ok(createVehicleUseCase.execute(request));
    }

    @GetMapping
    @Operation(summary = "List all vehicles", description = "Returns a list of all vehicles")
    public ResponseEntity<List<VehicleResponse>> list() {
        return ResponseEntity.ok(listVehiclesUseCase.execute());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "List vehicles by user", description = "Returns a list of vehicles owned by a specific user")
    public ResponseEntity<List<VehicleResponse>> listByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(listVehiclesByUserUseCase.execute(userId));
    }
}
