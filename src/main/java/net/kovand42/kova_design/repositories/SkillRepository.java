package net.kovand42.kova_design.repositories;

import net.kovand42.kova_design.entities.Category;
import net.kovand42.kova_design.entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findBySkillName(String skillName);
    List<Skill> findAllByCategoryOrderBySkillName(Category category);
}
