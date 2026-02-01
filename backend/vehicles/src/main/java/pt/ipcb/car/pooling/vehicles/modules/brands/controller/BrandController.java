package pt.ipcb.car.pooling.vehicles.modules.brands.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ipcb.car.pooling.vehicles.modules.brands.contracts.request.CreateBrandRequest;
import pt.ipcb.car.pooling.vehicles.modules.brands.contracts.response.BrandResponse;
import pt.ipcb.car.pooling.vehicles.modules.brands.useCases.CreateBrandUseCase;
import pt.ipcb.car.pooling.vehicles.modules.brands.useCases.GetAllBrandUseCase;
import pt.ipcb.car.pooling.vehicles.modules.brands.useCases.GetBrandByIdUseCase;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final CreateBrandUseCase createBrandUseCase;
    private final GetAllBrandUseCase getAllBrandUseCase;
    private final GetBrandByIdUseCase getBrandByIdUseCase;

    @GetMapping("")
    public ResponseEntity<List<BrandResponse>> getAll() {
        var data = getAllBrandUseCase.execute();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getById(@PathVariable UUID id) {
        var data = getBrandByIdUseCase.execute(id);
        return ResponseEntity.ok(data);
    }

    @PostMapping("")
    public ResponseEntity<BrandResponse> create(@Valid @RequestBody CreateBrandRequest request) {

        var response = createBrandUseCase.execute(request);

        return ResponseEntity
                .created(URI.create("/api/v1/brands/" + response.id()))
                .body(response);
    }
}
