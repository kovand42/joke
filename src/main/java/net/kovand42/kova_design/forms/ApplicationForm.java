package net.kovand42.kova_design.forms;

import net.kovand42.kova_design.entities.Repository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class ApplicationForm {
    @NotBlank
    private String applicationName;
    @NotNull
    private Repository repository;

    public ApplicationForm(String applicationName, Repository repository) {
        this.applicationName = applicationName;
        this.repository = repository;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Repository getRepository() {
        return repository;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApplicationForm)) return false;
        ApplicationForm that = (ApplicationForm) o;
        return getApplicationName().equalsIgnoreCase(that.getApplicationName()) &&
                getRepository().equals(that.getRepository());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getApplicationName(), getRepository());
    }

    @Override
    public String toString() {
        return "ApplicationForm{" +
                "applicationName='" + applicationName + '\'' +
                ", repository=" + repository +
                '}';
    }
}
