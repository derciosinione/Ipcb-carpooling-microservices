package pt.ipcb.car.pooling.vehicles.modules.models.controller;

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
import pt.ipcb.car.pooling.vehicles.modules.models.contracts.request.CreateModelRequest;
import pt.ipcb.car.pooling.vehicles.modules.models.contracts.response.ModelResponse;
import pt.ipcb.car.pooling.vehicles.modules.models.useCases.CreateModelUseCase;
import pt.ipcb.car.pooling.vehicles.modules.models.useCases.ListModelsUseCase;

import org.springframework.web.bind.annotation.PathVariable;
import pt.ipcb.car.pooling.vehicles.modules.models.useCases.ListModelsByBrandUseCase;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
@Tag(name = "Models", description = "Endpoints for managing vehicle models")
public class ModelController {

    private final CreateModelUseCase createModelUseCase;
    private final ListModelsUseCase listModelsUseCase;
    private final ListModelsByBrandUseCase listModelsByBrandUseCase;

    @PostMapping
    @Operation(summary = "Create a new model", description = "Creates a new vehicle model linked to a brand")
    public ResponseEntity<ModelResponse> create(@Valid @RequestBody CreateModelRequest request) {
        return ResponseEntity.ok(createModelUseCase.execute(request));
    }

    @GetMapping
    @Operation(summary = "List all models", description = "Returns a list of all vehicle models")
    public ResponseEntity<List<ModelResponse>> list() {
        return ResponseEntity.ok(listModelsUseCase.execute());
    }

    @GetMapping("/brand/{brandId}")
    @Operation(summary = "List models by brand", description = "Returns a list of vehicle models for a specific brand")
    public ResponseEntity<List<ModelResponse>> listByBrand(@PathVariable UUID brandId) {
        return ResponseEntity.ok(listModelsByBrandUseCase.execute(brandId));
    }
}
