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
@Table(name = "projectauthorities")
@NamedEntityGraph(name = ProjectAuthority.WITH_USERS,
        attributeNodes = {@NamedAttributeNode("usersWithAuth")})
public class ProjectAuthority implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String WITH_USERS="ProjectAuthority.withUsers";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long projectAuthorityId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
    @NotNull
    private Project project;
    @NotBlank
    private String projectAuthority;
    @ManyToMany
    @JoinTable(
            name = "projectuserauthorities",
            joinColumns = @JoinColumn(name = "projectAuthorityId"),
            inverseJoinColumns = @JoinColumn(name = "userId"))
    private Set<User> usersWithAuth = new LinkedHashSet<>();
    @Version
    private long version;

    public ProjectAuthority() {}

    public ProjectAuthority(@NotBlank String projectAuthority,
                            @NotNull Project project) {
        this.projectAuthority = projectAuthority;
        this.project = project;
    }

    public long getProjectAuthorityId() {
        return projectAuthorityId;
    }

    public Project getProject() {
        return project;
    }

    public String getProjectAuthority() {
        return projectAuthority;
    }

    public boolean addUser(User user){
        boolean added = usersWithAuth.add(user);
        if(!user.getProjectAuthorities().contains(this)){
            user.addProjectAuthority(this);
        }
        return added;
    }

    public boolean removeUser(User user){
        boolean removed = usersWithAuth.remove(user);
        if(user.getProjectAuthorities().contains(this)){
            user.removeProjectAuthority(this);
        }
        return removed;
    }

    public Set<User> getUsersWithAuth(){
        return Collections.unmodifiableSet(usersWithAuth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectAuthority)) return false;
        ProjectAuthority that = (ProjectAuthority) o;
        return getProject().equals(that.getProject()) &&
                getProjectAuthority().equals(that.getProjectAuthority());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProject(), getProjectAuthority());
    }

    @Override
    public String toString() {
        return "ProjectAuthority{" +
                "project=" + project +
                ", projectAuthority='" + projectAuthority + '\'' +
                '}';
    }
}
