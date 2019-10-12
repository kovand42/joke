package net.kovand42.kova_design.repositories;

import net.kovand42.kova_design.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(User.WITH_ROLES)
    Page<User> findAll(Pageable pageable);
    @Override
    @EntityGraph(User.WITH_ROLES)
    List<User> findAll();
    @EntityGraph(User.WITH_ROLES)
    Optional<User> findById(long id);
    @EntityGraph(User.WITH_ROLES)
    Optional<User> findByUsername(String username);
    @EntityGraph(User.WITH_ROLES)
    Optional<User> findByUsernameStartingWith(String username);
    @EntityGraph(User.WITH_ROLES)
    Optional<User> findByEmail(String email);
    @EntityGraph(User.WITH_ROLES)
    Optional<User> findByEmailStartingWith(String email);
}
