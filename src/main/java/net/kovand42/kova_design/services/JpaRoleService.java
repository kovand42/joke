package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Role;
import net.kovand42.kova_design.repositories.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly=true,isolation= Isolation.READ_COMMITTED)
public class JpaRoleService implements RoleService {
    private final RoleRepository roleRepository;

    public JpaRoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findById(long roleId) {
        return roleRepository.findById(roleId);
    }

    @Override
    public Optional<Role> findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void delete(Role role) {
        roleRepository.delete(role);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void create(Role role) {
        roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void update(Role role) {
        roleRepository.save(role);
    }
}
