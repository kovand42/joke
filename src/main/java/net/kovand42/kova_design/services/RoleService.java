package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Optional<Role> findById(long roleId);
    Optional<Role> findByRoleName(String roleName);
    List<Role> findAll();
    void delete(Role role);
    void create(Role role);
    void update(Role role);
}
