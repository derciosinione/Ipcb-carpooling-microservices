package pt.ipcb.car.pooling.vehicles.modules.brands.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.vehicles.exceptions.ResourceAlreadyExistsException;
import pt.ipcb.car.pooling.vehicles.modules.brands.contracts.request.CreateBrandRequest;
import pt.ipcb.car.pooling.vehicles.modules.brands.contracts.response.BrandResponse;
import pt.ipcb.car.pooling.vehicles.modules.brands.mapper.BrandMapper;
import pt.ipcb.car.pooling.vehicles.modules.brands.repository.IBrandRepository;

@Service
@RequiredArgsConstructor
public class CreateBrandUseCase {

    private final IBrandRepository repository;
    private final BrandMapper brandMapper;

    public BrandResponse execute(CreateBrandRequest request) {

        repository.findByName(request.name())
                .ifPresent(x -> {
                    throw new ResourceAlreadyExistsException();
                });

        var BrandEntity = brandMapper.toEntity(request);

        var savedBrand = repository.save(BrandEntity);

        return brandMapper.toResponse(savedBrand);
    }
}
