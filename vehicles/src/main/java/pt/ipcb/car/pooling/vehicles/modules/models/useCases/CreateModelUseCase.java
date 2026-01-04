package pt.ipcb.car.pooling.vehicles.modules.models.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.vehicles.modules.brands.entities.BrandEntity;
import pt.ipcb.car.pooling.vehicles.modules.brands.repository.IBrandRepository;
import pt.ipcb.car.pooling.vehicles.modules.models.contracts.request.CreateModelRequest;
import pt.ipcb.car.pooling.vehicles.modules.models.contracts.response.ModelResponse;
import pt.ipcb.car.pooling.vehicles.modules.models.entities.ModelEntity;
import pt.ipcb.car.pooling.vehicles.modules.models.mapper.ModelMapper;
import pt.ipcb.car.pooling.vehicles.modules.models.repository.ModelRepository;

@Service
@RequiredArgsConstructor
public class CreateModelUseCase {

        private final ModelRepository modelRepository;
        private final IBrandRepository brandRepository;
        private final ModelMapper modelMapper;

        public ModelResponse execute(CreateModelRequest request) {
                BrandEntity brand = brandRepository.findById(request.getBrandId())
                                .orElseThrow(() -> new RuntimeException("Brand not found"));

                ModelEntity model = modelMapper.toEntity(request, brand);

                ModelEntity savedModel = modelRepository.save(model);

                return modelMapper.toResponse(savedModel);
        }
}
