package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Application;
import net.kovand42.kova_design.entities.Repository;
import net.kovand42.kova_design.exceptions.RepositoryAlreadyExistsException;
import net.kovand42.kova_design.exceptions.RepositoryHasStillApplicationException;
import net.kovand42.kova_design.exceptions.RepositoryNotFoundException;
import net.kovand42.kova_design.repositories.ApplicationRepository;
import net.kovand42.kova_design.repositories.RepositoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Transactional(readOnly=true,isolation= Isolation.READ_COMMITTED)
public class JpaRepositoryService implements RepositoryService {
    private final RepositoryRepository repositoryRepository;
    private final ApplicationRepository applicationRepository;

    public JpaRepositoryService(RepositoryRepository repositoryRepository,
                                ApplicationRepository applicationRepository) {
        this.repositoryRepository = repositoryRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public Optional<Repository> findById(long repositoryId) {
        return repositoryRepository.findById(repositoryId);
    }

    @Override
    public Optional<Repository> findByUrl(String url) {
        return repositoryRepository.findByUrl(url);
    }

    @Override
    public List<Repository> findByRepositoryName(String repositoryName) {
        return repositoryRepository
                .findByRepositoryNameOrderByUrl(repositoryName);
    }

    @Override
    public List<Repository> findAll() {
        return repositoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void delete(Repository repository)
            throws RepositoryHasStillApplicationException, RepositoryNotFoundException{
        if(belongs(repository)){
            throw new RepositoryHasStillApplicationException();
        }
        if(!exists(repository)){
            throw new RepositoryNotFoundException();
        }
        repositoryRepository.delete(repository);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void create(Repository repository) throws RepositoryAlreadyExistsException {
        if(!exists(repository)){
            repositoryRepository.save(repository);
        }
        throw new RepositoryAlreadyExistsException();
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void update(Repository repository) {
        repositoryRepository.save(repository);
    }
    private boolean exists(Repository repository){
        AtomicBoolean exists = new AtomicBoolean(false);
        repositoryRepository.findAll().forEach(repository1 -> {
            if(repository1.getUrl().equalsIgnoreCase(repository.getUrl())){
                exists.set(true);
            }
        });
        return exists.get();
    }
    private boolean belongs(Repository repository){
        AtomicBoolean belongs = new AtomicBoolean(false);
        applicationRepository.findAll().forEach(application1 -> {
            if(application1.getRepository().equals(repository)){
                belongs.set(true);
            }
        });
        return belongs.get();
    }
}
