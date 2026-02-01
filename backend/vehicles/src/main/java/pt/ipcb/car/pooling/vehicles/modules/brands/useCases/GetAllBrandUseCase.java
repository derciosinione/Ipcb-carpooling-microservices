package pt.ipcb.car.pooling.vehicles.modules.brands.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.vehicles.modules.brands.contracts.response.BrandResponse;
import pt.ipcb.car.pooling.vehicles.modules.brands.mapper.BrandMapper;
import pt.ipcb.car.pooling.vehicles.modules.brands.repository.IBrandRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllBrandUseCase {
    final IBrandRepository repository;
    final BrandMapper brandMapper;

    public List<BrandResponse> execute() {
        return repository.findAll().stream()
                .map(brandMapper::toResponse)
                .toList();
    }
}
