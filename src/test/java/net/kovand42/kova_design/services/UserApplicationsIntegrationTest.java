package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Application;
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
@Sql("/applications.sql")
@Sql("/userapplications.sql")
public class UserApplicationsIntegrationTest
        extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    UserService userService;
    @Autowired
    SkillService skillService;
    @Autowired
    UserSkillService userSkillService;
    @Autowired
    ApplicationService applicationService;
    @Test
    public void userapplicationsBelongToUserskillAndApplication(){
        int userskillsInApplication1 = applicationService.findByApplicationName("test1")
                                                        .get(0).getUserSkills().size();
        int applicationsInUserSkill1 = userSkillService.findById(idTestUserSkill1_1())
                                                        .get().getApplications().size();
        assertEquals(4, userskillsInApplication1);
        assertEquals(1, applicationsInUserSkill1);
    }
    @Test
    public void adminCanAddUserToApplicationThroughUserapplication(){
        Application application = applicationService.findById(idTestApplication1v()).get();
        UserSkill userSkill = userSkillService.findById(idTestUserSkill1_2()).get();
        application.add(userSkill);
        assertTrue(userSkill.getApplications().contains(application));
    }
    @Test
    public void canNotAddUserskillToApplicationIfItIsAlreadyOnUserskillsList(){
        Application application = applicationService.findById(idTestApplication1()).get();
        UserSkill userSkill = userSkillService.findById(idTestUserSkill1_1()).get();
        boolean added = application.add(userSkill);
        assertFalse(added);
    }
    @Test
    public void canNotAddApplicationToUserskillIfItIsAlreadyOnApplicationsList(){
        Application application = applicationService.findById(idTestApplication1()).get();
        UserSkill userSkill = userSkillService.findById(idTestUserSkill1_1()).get();
        boolean added = userSkill.add(application);
        assertFalse(added);
    }
    @Test
    public void userapplicationToUserskillToUserIsUser(){
        Application application = applicationService.findById(idTestApplication1()).get();
        Set<UserSkill> userSkills = application.getUserSkills();
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
    private long idTestApplication1(){
        return super.jdbcTemplate.queryForObject(
                "select applicationId from applications where applicationName = 'test1'", Long.class);
    }
    private long idTestApplication1v(){
        return super.jdbcTemplate.queryForObject(
                "select applicationId from applications where applicationName = 'test1v'", Long.class);
    }
}
