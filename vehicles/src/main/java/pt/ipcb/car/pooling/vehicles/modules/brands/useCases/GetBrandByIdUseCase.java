package pt.ipcb.car.pooling.vehicles.modules.brands.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.vehicles.exceptions.ResourceNotFoundException;
import pt.ipcb.car.pooling.vehicles.modules.brands.contracts.response.BrandResponse;
import pt.ipcb.car.pooling.vehicles.modules.brands.mapper.BrandMapper;
import pt.ipcb.car.pooling.vehicles.modules.brands.repository.IBrandRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetBrandByIdUseCase {
    final IBrandRepository repository;
    final BrandMapper brandMapper;

    public BrandResponse execute(UUID id) {
        var profile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Profile with id %s not found", id)));

        return brandMapper.toResponse(profile);
    }
}
