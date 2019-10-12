package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Category;
import net.kovand42.kova_design.entities.Skill;

import java.util.List;
import java.util.Optional;

public interface SkillService {
    Optional<Skill> findById(long skillId);
    Optional<Skill> findBySkillName(String skillName);
    List<Skill> findAll();
    List<Skill> findByCategory(Category category);
    void delete(Skill skill);
    void create(Skill skill);
    void update(Skill skill);
}
