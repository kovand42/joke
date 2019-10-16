package net.kovand42.kova_design.repositories;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.Repository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ProjectRepository extends JpaRepository<Project, Long> {
    @EntityGraph(Project.WITH_USERSKILLS)
    Optional<Project> findById(long id);
    @Override
    @EntityGraph(Project.WITH_USERSKILLS)
    List<Project> findAll();
    @EntityGraph(Project.WITH_USERSKILLS)
    List<Project> findProjectByProjectName(String projectName);
    @EntityGraph(Project.WITH_USERSKILLS)
    List<Project> findProjectByRepositoryOrderByProjectName(Repository repository);
}
