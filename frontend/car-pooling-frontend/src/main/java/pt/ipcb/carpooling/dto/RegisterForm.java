package pt.ipcb.carpooling.dto;

import lombok.Data;

@Data
public class RegisterForm {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private boolean passenger;
    private boolean driver;
}
