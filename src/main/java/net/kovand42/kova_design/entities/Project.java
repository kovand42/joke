package net.kovand42.kova_design.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "projects")
@NamedEntityGraph(name = Project.WITH_USERSKILLS,
        attributeNodes = {@NamedAttributeNode("repository"),
                @NamedAttributeNode("userSkills")})
public class Project implements Serializable {
    public static final String WITH_USERSKILLS="Project.withUserSkills";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long projectId;
    @NotBlank
    private String projectName;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repositoryId")
    @NotNull
    private Repository repository;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "userprojects",
            joinColumns = @JoinColumn(name = "projectId"),
            inverseJoinColumns = @JoinColumn(name = "userSkillId"))
    private Set<UserSkill> userSkills = new LinkedHashSet<>();
    @Version
    private long version;

    protected Project() {}

    public Project(@NotBlank String projectName, @NotNull Repository repository) {
        this.projectName = projectName;
        this.repository = repository;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public Repository getRepository() {
        return repository;
    }

    public boolean add(UserSkill userSkill){
        boolean added = userSkills.add(userSkill);
        if(! userSkill.getProjects().contains(this)){
            userSkill.add(this);
        }
        return added;
    }

    public boolean remove(UserSkill userSkill){
        boolean removed = userSkills.remove(userSkill);
        if(userSkill.getProjects().contains(this)){
            userSkill.remove(this);
        }
        return removed;
    }

    public Set<UserSkill> getUserSkills(){
        return Collections.unmodifiableSet(userSkills);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project that = (Project) o;
        return getRepository().equals(that.getRepository()) &&
                getProjectName().equalsIgnoreCase(that.getProjectName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProjectName(), getRepository());
    }

    @Override
    public String toString() {
        return repository.toString() + " : " + projectName;
    }
}
