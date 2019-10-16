package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.Repository;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    List<Project> findAll();
    Optional<Project> findById(long projectId);
    List<Project> findByProjectName(String projectName);
    List<Project> findByRepository(Repository repository);
    void delete(Project project);
    void create(Project project);
    void update(Project project);
}
