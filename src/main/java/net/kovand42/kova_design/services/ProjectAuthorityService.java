package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.ProjectAuthority;

import java.util.List;
import java.util.Optional;

public interface ProjectAuthorityService {
    List<ProjectAuthority> findAll();
    List<ProjectAuthority> findAllByProject(Project project);
    Optional<ProjectAuthority> findByProjectAndAuthority(Project project, String authority);
    Optional<ProjectAuthority> findById(long id);
    void create(ProjectAuthority projectAuthority);
    void delete(ProjectAuthority projectAuthority);
    void update(ProjectAuthority projectAuthority);
}
