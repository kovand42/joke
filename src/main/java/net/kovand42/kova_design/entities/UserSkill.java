package net.kovand42.kova_design.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "userskills")
@NamedEntityGraph(name = UserSkill.WITH_PROJECTS,
attributeNodes = {@NamedAttributeNode("skill"),
        @NamedAttributeNode("projects"),
@NamedAttributeNode("user")})
public class UserSkill implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String WITH_PROJECTS = "UserSkill.withProjects";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userSkillId;
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId")
    private User user;
    @NotNull
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "skillId")
    private Skill skill;
    boolean validated;
    @ManyToMany(mappedBy = "userSkills")
    private Set<Project> projects = new LinkedHashSet<>();
    @Version
    private long version;

    public UserSkill() {}

    public UserSkill(@NotNull User user, @NotNull Skill skill, boolean validated) {
        this.user = user;
        this.skill = skill;
        this.validated = validated;
    }

    public boolean isValidated() {
        return validated;
    }

    public long getUserSkillId() {
        return userSkillId;
    }

    public User getUser() {
        return user;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public boolean add(Project project){
        boolean added = projects.add(project);
        if(!project.getUserSkills().contains(this)){
            project.add(this);
        }
        return added;
    }

    public boolean remove(Project project){
        boolean removed = projects.remove(project);
        if(project.getUserSkills().contains(this)){
            project.remove(this);
        }
        return removed;
    }

    public Set<Project> getProjects(){
        return Collections.unmodifiableSet(projects);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSkill)) return false;
        UserSkill userSkill = (UserSkill) o;
        return getUser().equals(userSkill.getUser()) &&
                getSkill().equals(userSkill.getSkill());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getSkill());
    }

    @Override
    public String toString() {
        return "UserSkill{" +
                "user=" + user +
                ", skill=" + skill +
                '}';
    }
}
