package net.kovand42.kova_design.forms;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class RoleForm {
    @NotBlank
    private String roleName;

    public RoleForm(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleForm)) return false;
        RoleForm roleForm = (RoleForm) o;
        return getRoleName().equals(roleForm.getRoleName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoleName());
    }

    @Override
    public String toString() {
        return roleName;
    }
}
