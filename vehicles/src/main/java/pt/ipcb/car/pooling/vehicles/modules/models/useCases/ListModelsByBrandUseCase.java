package pt.ipcb.car.pooling.vehicles.modules.models.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.vehicles.modules.models.contracts.response.ModelResponse;
import pt.ipcb.car.pooling.vehicles.modules.models.mapper.ModelMapper;
import pt.ipcb.car.pooling.vehicles.modules.models.repository.ModelRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListModelsByBrandUseCase {

    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    public List<ModelResponse> execute(UUID brandId) {
        return modelRepository.findByBrandId(brandId).stream()
                .map(modelMapper::toResponse)
                .collect(Collectors.toList());
    }
}
