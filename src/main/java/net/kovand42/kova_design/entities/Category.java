package net.kovand42.kova_design.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "categories")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long categoryId;
    @NotBlank
    @JoinColumn(name = "categoryName", unique = true)
    private String categoryName;
    @Version
    private long version;

    protected Category() {}

    public Category(@NotBlank String categoryName) {
        this.categoryName = categoryName;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return getCategoryName().equalsIgnoreCase(category.getCategoryName());
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
