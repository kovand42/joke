package net.kovand42.kova_design.forms;

import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class RepositoryForm {
    @NotBlank
    private String repositoryName;
    @URL
    @NotBlank
    private String url;

    public RepositoryForm(String repositoryName, String url) {
        this.repositoryName = repositoryName;
        this.url = url;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepositoryForm)) return false;
        RepositoryForm that = (RepositoryForm) o;
        return getUrl().equalsIgnoreCase(that.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }

    @Override
    public String toString() {
        return "RepositoryForm{" +
                "repositoryName='" + repositoryName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
