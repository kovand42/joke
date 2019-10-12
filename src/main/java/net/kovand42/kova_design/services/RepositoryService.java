package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Repository;

import java.util.List;
import java.util.Optional;

public interface RepositoryService {
    Optional<Repository> findById(long repositoryId);
    Optional<Repository> findByUrl(String url);
    List<Repository> findByRepositoryName(String repositoryName);
    List<Repository> findAll();
    void delete(Repository repository);
    void create(Repository repository);
    void update(Repository repository);
}
