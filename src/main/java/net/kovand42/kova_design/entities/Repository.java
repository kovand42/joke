package net.kovand42.kova_design.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "repositories")
public class Repository implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long repositoryId;
    @NotBlank
    private String repositoryName;
    @NotBlank
    @JoinColumn(name = "url", unique = true)
    private String url;
    @Version
    private long version;

    protected Repository() {}

    public Repository(@NotBlank String repositoryName, @NotBlank String url) {
        this.repositoryName = repositoryName;
        this.url = url;
    }

    public long getRepositoryId() {
        return repositoryId;
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
        if (!(o instanceof Repository)) return false;
        Repository that = (Repository) o;
        return getUrl().equalsIgnoreCase(that.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }

    @Override
    public String toString() {
        return "Repository{" +
                "repositoryName='" + repositoryName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
