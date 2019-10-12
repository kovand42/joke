package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Skill;
import net.kovand42.kova_design.entities.User;
import net.kovand42.kova_design.entities.UserSkill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserSkillService {
    List<UserSkill> findAll();
    Optional<UserSkill> findById(long userSkillId);
    List<UserSkill> findByUser(User user);
    List<UserSkill> findBySkill(Skill skill);
    void delete(UserSkill userSkill);
    void create(UserSkill userSkill);
    void update(UserSkill userSkill);
}
