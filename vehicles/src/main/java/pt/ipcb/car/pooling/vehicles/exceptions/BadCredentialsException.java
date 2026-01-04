package pt.ipcb.car.pooling.vehicles.exceptions;


import org.springframework.security.core.AuthenticationException;

public class BadCredentialsException extends AuthenticationException {
    public BadCredentialsException() {
        super("Invalid credentials");
    }

    public BadCredentialsException(String message) {
        super(message);
    }
}