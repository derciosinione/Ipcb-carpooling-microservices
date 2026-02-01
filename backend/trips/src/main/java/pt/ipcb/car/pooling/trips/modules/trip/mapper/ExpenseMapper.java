package pt.ipcb.car.pooling.trips.modules.trip.mapper;

import org.springframework.stereotype.Component;
import pt.ipcb.car.pooling.trips.modules.entities.ExpenseEntity;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.CreateExpenseRequest;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.ExpenseResponse;

@Component
public class ExpenseMapper {

    public ExpenseEntity toEntity(CreateExpenseRequest request){
        ExpenseEntity entity = new ExpenseEntity();
        entity.setAmount(request.getAmount());
        entity.setDescription(request.getDescription());
        entity.setType(request.getType());

        return entity;
    }

    public ExpenseResponse toResponse(ExpenseEntity entity){
        ExpenseResponse response = new ExpenseResponse();
    	response.setId(entity.getId());
    	response.setAmount(entity.getAmount());
    	response.setDescription(entity.getDescription());
    	response.setType(entity.getType());
        response.setRegisteredAt(entity.getCreatedAt());

        if (entity.getTrip() != null){
            response.setTripId(entity.getTrip().getId());
        }

    	return response;
    }
}
