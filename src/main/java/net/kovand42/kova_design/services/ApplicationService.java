package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Application;
import net.kovand42.kova_design.entities.Repository;

import java.util.List;
import java.util.Optional;

public interface ApplicationService {
    List<Application> findAll();
    Optional<Application> findById(long applicationId);
    List<Application> findByApplicationName(String applicationName);
    List<Application> findByRepository(Repository repository);
    void delete(Application application);
    void create(Application application);
    void update(Application application);
}
