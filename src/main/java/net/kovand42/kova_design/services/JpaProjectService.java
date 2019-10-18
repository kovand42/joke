package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.Repository;
import net.kovand42.kova_design.exceptions.ProjectAlreadyExistsException;
import net.kovand42.kova_design.exceptions.ProjectNotFoundException;
import net.kovand42.kova_design.repositories.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Transactional(readOnly=true,isolation= Isolation.READ_COMMITTED)
public class JpaProjectService implements ProjectService {
    private final ProjectRepository projectRepository;

    public JpaProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Optional<Project> findById(long projectId) {
        return projectRepository.findById(projectId);
    }

    @Override
    public List<Project> findByProjectName(String projectName) {
        return projectRepository
                .findProjectByProjectName(projectName);
    }

    @Override
    public List<Project> findByRepository(Repository repository) {
        return projectRepository
                .findProjectByRepositoryOrderByProjectName(repository);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void delete(Project project) throws ProjectNotFoundException {
        if(!exists(project)){
            throw new ProjectNotFoundException();
        }
        projectRepository.delete(project);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void create(Project project) throws ProjectAlreadyExistsException {
        if(exists(project)){
            throw new ProjectAlreadyExistsException();
        }
        projectRepository.save(project);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void update(Project project) {
        projectRepository.save(project);
    }
    private boolean exists(Project project){
        AtomicBoolean exists = new AtomicBoolean(false);
        projectRepository.findAll().forEach(project1 -> {
            if(project1.equals(project)){
                exists.set(true);
            }
        });
        return exists.get();
    }
}
