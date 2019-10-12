package net.kovand42.kova_design.forms;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class CategoryForm {
    @NotBlank
    private String categoryName;

    public CategoryForm(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryForm)) return false;
        CategoryForm that = (CategoryForm) o;
        return getCategoryName().equalsIgnoreCase(that.getCategoryName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCategoryName());
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
