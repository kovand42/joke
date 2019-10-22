package net.kovand42.kova_design.repositories;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.ProjectAuthority;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectAuthorityRepository extends JpaRepository<ProjectAuthority, Long> {
    @Override
    @EntityGraph(ProjectAuthority.WITH_USERS)
    List<ProjectAuthority> findAll();
    @EntityGraph(ProjectAuthority.WITH_USERS)
    Optional<ProjectAuthority> findById(long id);
    @EntityGraph(ProjectAuthority.WITH_USERS)
    List<ProjectAuthority> findProjectAuthoritiesByProjectOrderByProjectAuthorityId(Project project);
    @EntityGraph(ProjectAuthority.WITH_USERS)
    Optional<ProjectAuthority> findProjectAuthoritiesByProjectAndProjectAuthorityOrderByProjectAuthorityId(Project project, String authority);
}
