package net.kovand42.kova_design.forms;

import net.kovand42.kova_design.entities.Repository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class ProjectForm {
    @NotBlank
    private String projectName;
    @NotNull
    private Repository repository;

    public ProjectForm(String projectName, Repository repository) {
        this.projectName = projectName;
        this.repository = repository;
    }

    public String getProjectName() {
        return projectName;
    }

    public Repository getRepository() {
        return repository;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectForm)) return false;
        ProjectForm that = (ProjectForm) o;
        return getProjectName().equalsIgnoreCase(that.getProjectName()) &&
                getRepository().equals(that.getRepository());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProjectName(), getRepository());
    }

    @Override
    public String toString() {
        return "ProjectForm{" +
                "projectName='" + projectName + '\'' +
                ", repository=" + repository +
                '}';
    }
}
