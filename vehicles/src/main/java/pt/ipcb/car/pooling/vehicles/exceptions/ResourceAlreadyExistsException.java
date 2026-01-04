package pt.ipcb.car.pooling.vehicles.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException() {
        super("Resource already exists");
    }

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}