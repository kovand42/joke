package net.kovand42.kova_design.forms;

import net.kovand42.kova_design.entities.Category;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class SkillForm {
    @NotBlank
    private String skillName;
    @NotNull
    private Category category;

    public SkillForm(String skillName, Category category) {
        this.skillName = skillName;
        this.category = category;
    }

    public String getSkillName() {
        return skillName;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkillForm)) return false;
        SkillForm skillForm = (SkillForm) o;
        return getSkillName().equalsIgnoreCase(skillForm.getSkillName());
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
