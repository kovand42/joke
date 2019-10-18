package net.kovand42.kova_design.repositories;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.Request;
import net.kovand42.kova_design.entities.User;
import net.kovand42.kova_design.valueobjects.RequestIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, RequestIdentity> {
    List<Request> findRequestsByRequestIdentityProjectOrderByRequestIdentity(Project project);
    List<Request> findRequestsByRequestIdentityUserOrderByRequestIdentity(User user);
}
