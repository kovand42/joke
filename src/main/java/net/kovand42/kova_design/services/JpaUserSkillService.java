package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Skill;
import net.kovand42.kova_design.entities.User;
import net.kovand42.kova_design.entities.UserSkill;
import net.kovand42.kova_design.exceptions.UserSkillAlreadyExistsException;
import net.kovand42.kova_design.exceptions.UserSkillNotFoundException;
import net.kovand42.kova_design.repositories.UserSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
public class JpaUserSkillService implements UserSkillService {
    private final UserSkillRepository userSkillRepository;

    public JpaUserSkillService(UserSkillRepository userSkillRepository) {
        this.userSkillRepository = userSkillRepository;
    }

    @Override
    //@Transactional(propagation= Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
    public List<UserSkill> findAll() {
        return userSkillRepository.findAll();
    }

    @Override
    //@Transactional(propagation=Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
    public Optional<UserSkill> findById(long userSkillId) {
        return userSkillRepository.findById(userSkillId);
    }

    @Override
    //@Transactional(propagation=Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
    public List<UserSkill> findByUser(User user) {
        return userSkillRepository
                .findUserSkillByUserOrderByUserSkillId(user);
    }

    @Override
    //@Transactional(propagation=Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
    public List<UserSkill> findBySkill(Skill skill) {
        return userSkillRepository
                .findUserSkillBySkillOrderByUserSkillId(skill);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void delete(UserSkill userSkill)
            throws UserSkillNotFoundException{
        if(contains(userSkill)){
            userSkillRepository.delete(userSkill);
        }
        throw new UserSkillNotFoundException();
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void create(UserSkill userSkill)
            throws UserSkillAlreadyExistsException {
        if(contains(userSkill)){
            throw new UserSkillAlreadyExistsException();
        }
        userSkillRepository.save(userSkill);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void update(UserSkill userSkill) {
        userSkillRepository.save(userSkill);
    }

    private boolean contains(UserSkill userSkill){
        List<UserSkill> userSkills = findByUser(userSkill.getUser());
        AtomicBoolean contains = new AtomicBoolean(false);
        userSkills.forEach(userSkill1 -> {
            if (userSkill1.getSkill().getSkillName().equals(userSkill.getSkill().getSkillName())){
                contains.set(true);
            }
        });
        return contains.get();
    }
}
