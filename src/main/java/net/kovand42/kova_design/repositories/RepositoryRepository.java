package net.kovand42.kova_design.repositories;

import net.kovand42.kova_design.entities.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepositoryRepository extends JpaRepository <Repository, Long> {
    Optional<Repository> findByUrl(String url);
    List<Repository> findByRepositoryNameOrderByUrl(String repositoryName);
}
