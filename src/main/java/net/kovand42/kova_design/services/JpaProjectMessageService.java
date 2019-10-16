package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.ProjectMessage;
import net.kovand42.kova_design.entities.User;
import net.kovand42.kova_design.repositories.ProjectMessageRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly=true,isolation= Isolation.READ_COMMITTED)
public class JpaProjectMessageService implements ProjectMessageService{
    private final ProjectMessageRepository projectMessageRepository;

    public JpaProjectMessageService(ProjectMessageRepository projectMessageRepository) {
        this.projectMessageRepository = projectMessageRepository;
    }

    @Override
    @EntityGraph(ProjectMessage.WITH_USER_AND_PROJECT)
    public Optional<ProjectMessage> findById(long id) {
        return projectMessageRepository.findById(id);
    }

    @Override
    @EntityGraph(ProjectMessage.WITH_USER_AND_PROJECT)
    public List<ProjectMessage> findAll() {
        return projectMessageRepository.findAll();
    }

    @Override
    @EntityGraph(ProjectMessage.WITH_USER_AND_PROJECT)
    public List<ProjectMessage> findByUser(User user) {
        return projectMessageRepository.findProjectMessageByUserOrderByMessageDateTime(user);
    }

    @Override
    @EntityGraph(ProjectMessage.WITH_USER_AND_PROJECT)
    public List<ProjectMessage> findByProject(Project project) {
        return projectMessageRepository.findProjectMessageByProjectOrderByMessageDateTime(project);
    }

    @Override
    @EntityGraph(ProjectMessage.WITH_USER_AND_PROJECT)
    public List<ProjectMessage> findByUserAndProject(User user, Project project) {
        return projectMessageRepository.findProjectMessageByUserAndProjectOrderByMessageDateTime(user, project);
    }

    @Override
    @Transactional(readOnly=false,isolation= Isolation.READ_COMMITTED)
    public void create(ProjectMessage projectMessage) {
        projectMessageRepository.save(projectMessage);
    }

    @Override
    @Transactional(readOnly=false,isolation= Isolation.READ_COMMITTED)
    public void update(ProjectMessage projectMessage) {
        projectMessageRepository.save(projectMessage);
    }

    @Override
    @Transactional(readOnly=false,isolation= Isolation.READ_COMMITTED)
    public void delete(ProjectMessage projectMessage) {
        projectMessageRepository.delete(projectMessage);
    }
}
