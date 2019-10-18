package net.kovand42.kova_design.forms;

import net.kovand42.kova_design.entities.Project;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class ProjectAuthorityForm {
    @NotNull
    private Project project;
    @NotBlank
    private String projectAuthority;

    public ProjectAuthorityForm(Project project, String projectAuthority) {
        this.project = project;
        this.projectAuthority = projectAuthority;
    }

    public Project getProject() {
        return project;
    }

    public String getProjectAuthority() {
        return projectAuthority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectAuthorityForm)) return false;
        ProjectAuthorityForm that = (ProjectAuthorityForm) o;
        return getProject().equals(that.getProject()) &&
                getProjectAuthority().equals(that.getProjectAuthority());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProject(), getProjectAuthority());
    }
}
