package net.kovand42.kova_design.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "skills")
public class Skill implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long skillId;
    @NotBlank
    @JoinColumn(name = "skillName", unique = true)
    private String skillName;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoryId")
    @NotNull
    private Category category;
    private boolean validated;
    @Version
    private long version;

    public Skill() {}

    public Skill(@NotBlank String skillName, @NotNull Category category, boolean validated) {
        this.skillName = skillName;
        this.category = category;
        this.validated = validated;
    }

    public long getSkillId() {
        return skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Skill)) return false;
        Skill skill = (Skill) o;
        return getSkillName().equalsIgnoreCase(skill.getSkillName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSkillName());
    }

    @Override
    public String toString() {
        return skillName;
    }
}
