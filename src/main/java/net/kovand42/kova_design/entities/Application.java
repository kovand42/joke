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
@Table(name = "applications")
@NamedEntityGraph(name = Application.WITH_USERSKILLS,
        attributeNodes = {@NamedAttributeNode("repository"),
                @NamedAttributeNode("userSkills")})
public class Application implements Serializable {
    public static final String WITH_USERSKILLS="Application.withUserSkills";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long applicationId;
    @NotBlank
    private String applicationName;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repositoryId")
    @NotNull
    private Repository repository;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "userapplications",
            joinColumns = @JoinColumn(name = "applicationId"),
            inverseJoinColumns = @JoinColumn(name = "userSkillId"))
    private Set<UserSkill> userSkills = new LinkedHashSet<>();
    @Version
    private long version;

    protected Application() {}

    public Application(@NotBlank String applicationName, @NotNull Repository repository) {
        this.applicationName = applicationName;
        this.repository = repository;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Repository getRepository() {
        return repository;
    }

    public boolean add(UserSkill userSkill){
        boolean added = userSkills.add(userSkill);
        if(! userSkill.getApplications().contains(this)){
            userSkill.add(this);
        }
        return added;
    }

    public boolean remove(UserSkill userSkill){
        boolean removed = userSkills.remove(userSkill);
        if(userSkill.getApplications().contains(this)){
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
        if (!(o instanceof Application)) return false;
        Application that = (Application) o;
        return getRepository().equals(that.getRepository()) &&
                getApplicationName().equalsIgnoreCase(that.getApplicationName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getApplicationName(), getRepository());
    }

    @Override
    public String toString() {
        return repository.toString() + " : " + applicationName;
    }
}
