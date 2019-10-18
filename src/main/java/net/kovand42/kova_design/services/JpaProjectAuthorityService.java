package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.ProjectAuthority;
import net.kovand42.kova_design.repositories.ProjectAuthorityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JpaProjectAuthorityService implements ProjectAuthorityService{
    private final ProjectAuthorityRepository projectAuthorityRepository;

    public JpaProjectAuthorityService(ProjectAuthorityRepository projectAuthorityRepository) {
        this.projectAuthorityRepository = projectAuthorityRepository;
    }

    @Override
    public List<ProjectAuthority> findAll() {
        return projectAuthorityRepository.findAll();
    }

    @Override
    public List<ProjectAuthority> findAllByProject(Project project) {
        return projectAuthorityRepository
                .findProjectAuthoritiesByProjectOrderByProjectAuthorityId(project);
    }

    @Override
    public List<ProjectAuthority> findByProjectAndAuthority(Project project, String authority) {
        return projectAuthorityRepository
                .findProjectAuthoritiesByProjectAndProjectAuthorityOrderByProjectAuthorityId(project, authority);
    }

    @Override
    public Optional<ProjectAuthority> findById(long id) {
        return projectAuthorityRepository.findById(id);
    }
}
