package net.kovand42.kova_design.repositories;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.Request;
import net.kovand42.kova_design.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @EntityGraph(Request.WITH_USER_And_PROJECT)
    List<Request> findRequestsByProjectOrderByProject(Project project);
    @EntityGraph(Request.WITH_USER_And_PROJECT)
    List<Request> findRequestsByUserOrderByUser(User user);
    @EntityGraph(Request.WITH_USER_And_PROJECT)
    List<Request> findRequestsByProjectAndUser(Project project, User user);
}
