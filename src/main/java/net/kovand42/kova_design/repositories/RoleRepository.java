package net.kovand42.kova_design.repositories;

import net.kovand42.kova_design.entities.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Override
    @EntityGraph(Role.WITH_USERS)
    List<Role> findAll();
    @EntityGraph(Role.WITH_USERS)
    Optional<Role> findById(long id);
    @EntityGraph(Role.WITH_USERS)
    Optional<Role> findByRoleName(String roleName);
}
