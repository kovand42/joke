package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.User;
import net.kovand42.kova_design.entities.UserSkill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Comparator;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("/users.sql")
@Sql("/categories.sql")
@Sql("/skills.sql")
@Sql("/userskills.sql")
@Sql("/repositories.sql")
@Sql("/projects.sql")
@Sql("/userprojects.sql")
public class UserProjectsIntegrationTest
        extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    UserService userService;
    @Autowired
    SkillService skillService;
    @Autowired
    UserSkillService userSkillService;
    @Autowired
    ProjectService projectService;
    @Test
    public void userprojectsBelongToUserskillAndProject(){
        int userskillsInProject1 = projectService.findByProjectName("test1")
                                                        .get(0).getUserSkills().size();
        int projectsInUserSkill1 = userSkillService.findById(idTestUserSkill1_1())
                                                        .get().getProjects().size();
        assertEquals(4, userskillsInProject1);
        assertEquals(1, projectsInUserSkill1);
    }
    @Test
    public void userCanAddUserToProjectThroughUserproject(){
        Project project = projectService.findById(idTestProject1v()).get();
        UserSkill userSkill = userSkillService.findById(idTestUserSkill1_2()).get();
        project.add(userSkill);
        assertTrue(userSkill.getProjects().contains(project));
    }
    @Test
    public void canNotAddUserskillToProjectIfItIsAlreadyOnUserskillsList(){
        Project project = projectService.findById(idTestProject1()).get();
        UserSkill userSkill = userSkillService.findById(idTestUserSkill1_1()).get();
        boolean added = project.add(userSkill);
        assertFalse(added);
    }
    @Test
    public void canNotAddProjectToUserskillIfItIsAlreadyOnProjectsList(){
        Project project = projectService.findById(idTestProject1()).get();
        UserSkill userSkill = userSkillService.findById(idTestUserSkill1_1()).get();
        boolean added = userSkill.add(project);
        assertFalse(added);
    }
    @Test
    public void userprojectToUserskillToUserIsUser(){
        Project project = projectService.findById(idTestProject1()).get();
        Set<UserSkill> userSkills = project.getUserSkills();
        UserSkill userSkill = userSkills.stream()
                .sorted(Comparator.comparing(UserSkill::getUserSkillId))
                .findFirst().get();
        User user = userSkill.getUser();
        assertEquals(user, userService.findById(idTestUser1()).get());
    }
    private long idTestUser1(){
        return super.jdbcTemplate.queryForObject(
                "select id from users where username = 'test1'", Long.class);
    }
    private long idTestUserSkill1_1(){
        return super.jdbcTemplate.queryForObject(
                "select userSkillId from userskills where" +
                        " skillId = (select skillId from skills where skillName = 'test1')" +
                        " and userId = (select id from users where username = 'test1')", Long.class);
    }
    private long idTestUserSkill1_2(){
        return super.jdbcTemplate.queryForObject(
                "select userSkillId from userskills where" +
                        " skillId = (select skillId from skills where skillName = 'test2')" +
                        " and userId = (select id from users where username = 'test1')", Long.class);
    }
    private long idTestProject1(){
        return super.jdbcTemplate.queryForObject(
                "select projectId from projects where projectName = 'test1'", Long.class);
    }
    private long idTestProject1v(){
        return super.jdbcTemplate.queryForObject(
                "select projectId from projects where projectName = 'test1v'", Long.class);
    }
}
