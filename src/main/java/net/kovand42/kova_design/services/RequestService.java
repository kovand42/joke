package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.Request;
import net.kovand42.kova_design.entities.User;

import java.util.List;
import java.util.Optional;

public interface RequestService {
    List<Request> findAll();
    List<Request> findByProject(Project project);
    List<Request> findByUser(User user);
    List<Request> findByProjectAndUser(Project project, User user);
    Optional<Request> findById(long id);
    void create(Request request);
    void delete(Request request);
    void update(Request request);
}
