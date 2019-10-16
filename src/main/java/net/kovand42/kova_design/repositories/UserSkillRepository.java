package net.kovand42.kova_design.repositories;

import net.kovand42.kova_design.entities.Skill;
import net.kovand42.kova_design.entities.User;
import net.kovand42.kova_design.entities.UserSkill;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    @EntityGraph(UserSkill.WITH_PROJECTS)
    Optional<UserSkill> findById(long id);
    @Override
    @EntityGraph(UserSkill.WITH_PROJECTS)
    List<UserSkill> findAll();
    @EntityGraph(UserSkill.WITH_PROJECTS)
    List<UserSkill> findUserSkillByUserOrderByUserSkillId(User user);
    @EntityGraph(UserSkill.WITH_PROJECTS)
    List<UserSkill> findUserSkillBySkillOrderByUserSkillId(Skill skill);
}
