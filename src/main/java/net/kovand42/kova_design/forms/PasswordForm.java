package net.kovand42.kova_design.forms;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class PasswordForm {
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;

    public PasswordForm(String password, String confirmPassword) {
        this.password = password;
        this.confirmPassword = confirmPassword;
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
        if (!(o instanceof PasswordForm)) return false;
        PasswordForm that = (PasswordForm) o;
        return getPassword().equals(that.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPassword());
    }

    @Override
    public String toString() {
        return password;
    }
}
