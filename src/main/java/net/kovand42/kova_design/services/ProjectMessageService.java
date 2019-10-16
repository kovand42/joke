package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.ProjectMessage;
import net.kovand42.kova_design.entities.User;

import java.util.List;
import java.util.Optional;

public interface ProjectMessageService {
    Optional<ProjectMessage> findById(long id);
    List<ProjectMessage> findAll();
    List<ProjectMessage> findByUser(User user);
    List<ProjectMessage> findByProject(Project project);
    List<ProjectMessage> findByUserAndProject(User user, Project project);
    void create(ProjectMessage projectMessage);
    void update(ProjectMessage projectMessage);
    void delete(ProjectMessage projectMessage);
}
