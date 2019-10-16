package net.kovand42.kova_design.repositories;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.ProjectMessage;
import net.kovand42.kova_design.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMessageRepository extends JpaRepository <ProjectMessage, Long> {
    List<ProjectMessage> findProjectMessageByUserOrderByMessageDateTime(User user);
    List<ProjectMessage> findProjectMessageByProjectOrderByMessageDateTime(Project project);
    List<ProjectMessage> findProjectMessageByUserAndProjectOrderByMessageDateTime(User user, Project project);
}
