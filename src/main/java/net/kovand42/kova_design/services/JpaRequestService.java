package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.Request;
import net.kovand42.kova_design.entities.User;
import net.kovand42.kova_design.repositories.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly=true,isolation= Isolation.READ_COMMITTED)
public class JpaRequestService implements RequestService {
    private final RequestRepository requestRepository;

    public JpaRequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public List<Request> findAll() {
        return requestRepository.findAll();
    }

    @Override
    public List<Request> findByProject(Project project) {
        return requestRepository.findRequestsByProjectOrderByProject(project);
    }

    @Override
    public List<Request> findByUser(User user) {
        return requestRepository.findRequestsByUserOrderByUser(user);
    }

    @Override
    public List<Request> findByProjectAndUser(Project project, User user) {
        return requestRepository.findRequestsByProjectAndUser(project, user);
    }

    @Override
    public Optional<Request> findById(long id) {
        return requestRepository.findById(id);
    }

    @Override
    @Transactional(readOnly=false,isolation= Isolation.READ_COMMITTED)
    public void create(Request request) {
        requestRepository.save(request);
    }

    @Override
    @Transactional(readOnly=false,isolation= Isolation.READ_COMMITTED)
    public void delete(Request request) {
        requestRepository.delete(request);
    }

    @Override
    @Transactional(readOnly=false,isolation= Isolation.READ_COMMITTED)
    public void update(Request request) {
        requestRepository.save(request);
    }
}
