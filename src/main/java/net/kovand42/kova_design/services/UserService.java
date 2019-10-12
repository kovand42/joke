package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Role;
import net.kovand42.kova_design.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Page<User> findAll(Pageable pageable);
    Optional<User> findById(long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameStartingWith(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailStartingWith(String email);
    void addRoleToUser(Role role, User user);
    void delete(User user);
    void create(User user);
    void update(User user);
}
