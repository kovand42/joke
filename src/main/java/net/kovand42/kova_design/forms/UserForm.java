package net.kovand42.kova_design.forms;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class UserForm {
    @NotBlank
    private String username;
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;


    public UserForm(String username, String email,
                    String password, String confirmPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public boolean validatePaswoord(String password, String confirmPassword){
        if(password.equals(confirmPassword)){
            this.password = password;
            this.confirmPassword = confirmPassword;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserForm)) return false;
        UserForm userForm = (UserForm) o;
        return getUsername().equalsIgnoreCase(userForm.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername());
    }
}
