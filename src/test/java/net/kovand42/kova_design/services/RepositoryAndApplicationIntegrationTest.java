package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Application;
import net.kovand42.kova_design.entities.Repository;
import net.kovand42.kova_design.exceptions.ApplicationAlreadyExistsException;
import net.kovand42.kova_design.exceptions.RepositoryAlreadyExistsException;
import net.kovand42.kova_design.exceptions.RepositoryHasStillApplicationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("/repositories.sql")
@Sql("/applications.sql")
public class RepositoryAndApplicationIntegrationTest
        extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    ApplicationService applicationService;

    @Test(expected = RepositoryAlreadyExistsException.class)
    public void urlHasToBeUnique(){
        Repository repository = new Repository("test10", "test1url");
        repositoryService.create(repository);
    }
    @Test(expected = RepositoryHasStillApplicationException.class)
    public void canNotDeleteRepositoryUsedByApplications(){
        repositoryService.delete(repositoryService.findById(idTestRepository1()).get());
    }
    @Test(expected = ApplicationAlreadyExistsException.class)
    public void applicationNameAndRepositoryCanNotBeIdenticalAtTheSameTime(){
        Application application = new Application("test1",
                repositoryService.findById(idTestRepository1()).get());
        applicationService.create(application);

    }
    @Test
    public void applicationNameCanBeIdenticalIfRepositoriesAreDifferent(){
        int applicationsSize = applicationService.findAll().size();
        Application application = new Application("test1",
                repositoryService.findById(idTestRepository2()).get());
        applicationService.create(application);
        assertEquals(applicationsSize + 1, applicationService.findAll().size());
    }
    @Test
    public void repositoryCanBeIdenticalIfApplicationNamesAreDifferent(){
        int applicationsSize = applicationService.findAll().size();
        Application application = new Application("test10",
                repositoryService.findById(idTestRepository1()).get());
        applicationService.create(application);
        assertEquals(applicationsSize + 1, applicationService.findAll().size());
    }
    @Test
    public void deleteRepositoryWorksAfterDeletingLastApplicationFromThatRepository(){
        int applicationsSize = applicationService.findAll().size();
        int repositoriesSize = repositoryService.findAll().size();
        applicationService.delete(
                applicationService.findByApplicationName("test2").get(0));
        repositoryService.delete(
                repositoryService.findById(idTestRepository2()).get());
        assertEquals(applicationsSize - 1, applicationService.findAll().size());
        assertEquals(repositoriesSize - 1, repositoryService.findAll().size());
    }
    private long idTestRepository1(){
        return super.jdbcTemplate.queryForObject(
                "select repositoryId from repositories where repositoryName = 'test1'", Long.class);
    }
    private long idTestRepository2(){
        return super.jdbcTemplate.queryForObject(
                "select repositoryId from repositories where repositoryName = 'test2'", Long.class);
    }
}
