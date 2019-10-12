package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Category;
import net.kovand42.kova_design.entities.Skill;
import net.kovand42.kova_design.repositories.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
public class JpaSkillService implements SkillService{
    private final SkillRepository skillRepository;

    public JpaSkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public Optional<Skill> findById(long skillId) {
        return skillRepository.findById(skillId);
    }

    @Override
    public Optional<Skill> findBySkillName(String skillName) {
        return skillRepository.findBySkillName(skillName);
    }

    @Override
    public List<Skill> findAll() {
        return skillRepository.findAll();
    }

    @Override
    public List<Skill> findByCategory(Category category) {
        return skillRepository.findAllByCategoryOrderBySkillName(category);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void delete(Skill skill){
        skillRepository.delete(skill);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void create(Skill skill){
        skillRepository.save(skill);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void update(Skill skill) {
        skillRepository.save(skill);
    }
}
