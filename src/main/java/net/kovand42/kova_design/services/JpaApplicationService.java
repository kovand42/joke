package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Application;
import net.kovand42.kova_design.entities.Repository;
import net.kovand42.kova_design.exceptions.ApplicationAlreadyExistsException;
import net.kovand42.kova_design.exceptions.ApplicationNotFoundException;
import net.kovand42.kova_design.repositories.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Transactional(readOnly=true,isolation= Isolation.READ_COMMITTED)
public class JpaApplicationService implements ApplicationService {
    private final ApplicationRepository applicationRepository;

    public JpaApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Override
    //@Transactional(propagation= Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

    @Override
    //@Transactional(propagation=Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
    public Optional<Application> findById(long applicationId) {
        return applicationRepository.findById(applicationId);
    }

    @Override
    //@Transactional(propagation=Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
    public List<Application> findByApplicationName(String applicationName) {
        return applicationRepository
                .findApplicationByApplicationName(applicationName);
    }

    @Override
    //@Transactional(propagation=Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
    public List<Application> findByRepository(Repository repository) {
        return applicationRepository
                .findApplicationByRepositoryOrderByApplicationName(repository);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void delete(Application application) throws ApplicationNotFoundException {
        if(!exists(application)){
            throw new ApplicationNotFoundException();
        }
        applicationRepository.delete(application);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void create(Application application) throws ApplicationAlreadyExistsException {
        if(exists(application)){
            throw new ApplicationAlreadyExistsException();
        }
        applicationRepository.save(application);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void update(Application application) {
        applicationRepository.save(application);
    }
    private boolean exists(Application application){
        AtomicBoolean exists = new AtomicBoolean(false);
        applicationRepository.findAll().forEach(application1 -> {
            if(application1.equals(application)){
                exists.set(true);
            }
        });
        return exists.get();
    }
}
