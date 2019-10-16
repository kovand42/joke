package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.Repository;
import net.kovand42.kova_design.exceptions.ProjectAlreadyExistsException;
import net.kovand42.kova_design.exceptions.RepositoryAlreadyExistsException;
import net.kovand42.kova_design.exceptions.RepositoryHasStillProjectException;
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
@Sql("/projects.sql")
public class RepositoryAndProjectIntegrationTest
        extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    ProjectService projectService;

    @Test(expected = RepositoryAlreadyExistsException.class)
    public void urlHasToBeUnique(){
        Repository repository = new Repository("test10", "test1url");
        repositoryService.create(repository);
    }
    @Test(expected = RepositoryHasStillProjectException.class)
    public void canNotDeleteRepositoryUsedByProjects(){
        repositoryService.delete(repositoryService.findById(idTestRepository1()).get());
    }
    @Test(expected = ProjectAlreadyExistsException.class)
    public void projectNameAndRepositoryCanNotBeIdenticalAtTheSameTime(){
        Project project = new Project("test1",
                repositoryService.findById(idTestRepository1()).get());
        projectService.create(project);

    }
    @Test
    public void projectNameCanBeIdenticalIfRepositoriesAreDifferent(){
        int projectsSize = projectService.findAll().size();
        Project project = new Project("test1",
                repositoryService.findById(idTestRepository2()).get());
        projectService.create(project);
        assertEquals(projectsSize + 1, projectService.findAll().size());
    }
    @Test
    public void repositoryCanBeIdenticalIfProjectNamesAreDifferent(){
        int projectsSize = projectService.findAll().size();
        Project project = new Project("test10",
                repositoryService.findById(idTestRepository1()).get());
        projectService.create(project);
        assertEquals(projectsSize + 1, projectService.findAll().size());
    }
    @Test
    public void deleteRepositoryWorksAfterDeletingLastProjectFromThatRepository(){
        int projectsSize = projectService.findAll().size();
        int repositoriesSize = repositoryService.findAll().size();
        projectService.delete(
                projectService.findByProjectName("test2").get(0));
        repositoryService.delete(
                repositoryService.findById(idTestRepository2()).get());
        assertEquals(projectsSize - 1, projectService.findAll().size());
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
