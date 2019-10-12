package net.kovand42.kova_design.repositories;

import net.kovand42.kova_design.entities.Application;
import net.kovand42.kova_design.entities.Repository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ApplicationRepository extends JpaRepository<Application, Long> {
    @EntityGraph(Application.WITH_USERSKILLS)
    Optional<Application> findById(long id);
    @Override
    @EntityGraph(Application.WITH_USERSKILLS)
    List<Application> findAll();
    @EntityGraph(Application.WITH_USERSKILLS)
    List<Application> findApplicationByApplicationName(String applicationName);
    @EntityGraph(Application.WITH_USERSKILLS)
    List<Application> findApplicationByRepositoryOrderByApplicationName(Repository repository);
}
