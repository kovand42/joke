package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Category;
import net.kovand42.kova_design.entities.Skill;
import net.kovand42.kova_design.entities.User;
import net.kovand42.kova_design.entities.UserSkill;
import net.kovand42.kova_design.exceptions.UserSkillAlreadyExistsException;
import net.kovand42.kova_design.exceptions.UserSkillNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("/users.sql")
@Sql("/categories.sql")
@Sql("/skills.sql")
@Sql("/userskills.sql")
public class UserSkillUserAndSkillIntegrationTest
        extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    CategoryService categoryService;
    @Autowired
    SkillService skillService;
    @Autowired
    UserService userService;
    @Autowired
    UserSkillService userSkillService;

    @Test
    public void findAll(){
        assertTrue( userSkillService.findAll().size() > 0);
    }
    @Test
    public void findByUser(){
        assertEquals(3, userSkillService.findByUser(userService.findById(idTestUser1()).get()).size());
        assertEquals(2, userSkillService.findByUser(userService.findById(idTestUser2()).get()).size());
        assertEquals(1, userSkillService.findByUser(userService.findById(idTestUser3()).get()).size());
    }
    @Test
    public void skillsInUserSkillsCanBeIdenticalIfTheyBelongToDifferentUser(){
        assertEquals(userSkillService.findByUser(userService.findById(idTestUser1()).get()).get(0).getSkill(),
                userSkillService.findByUser(userService.findById(idTestUser2()).get()).get(0).getSkill());
        assertEquals(userSkillService.findByUser(userService.findById(idTestUser1()).get()).get(1).getSkill(),
                userSkillService.findByUser(userService.findById(idTestUser2()).get()).get(1).getSkill());
        assertEquals(userSkillService.findByUser(userService.findById(idTestUser1()).get()).get(2).getSkill(),
                userSkillService.findByUser(userService.findById(idTestUser3()).get()).get(0).getSkill());
    }
    @Test(expected = UserSkillAlreadyExistsException.class)
    public void skillsInUserSkillsCanNotBeIdenticalIfTheyBelongToTheSameUser(){
       UserSkill userSkill =
                new UserSkill(userService.findById(idTestUser1()).get(),
                skillService.findById(idTestSkill1()).get(), false);
        userSkillService.create(userSkill);
    }
    @Test(expected = UserSkillNotFoundException.class)
    public void canNotDeleteUserSkillThatDoesNotExists(){
        categoryService.create(
                new Category("test10"));
        long idTestCategory10 = super.jdbcTemplate.queryForObject(
                "select categoryId from categories where" +
                        " categoryName = 'test10'", Long.class);
        skillService.create(
                new Skill("test10",
                categoryService.findById(idTestCategory10).get(),
                        false));
        userService.create(
                new User("test10",
                        "test10@test10",
                        "test10",
                        true));
        userSkillService.delete(
                new UserSkill(userService.findByUsername("test10").get(),
                        skillService.findBySkillName("test10").get(), false));
   }
    private long idTestSkill1(){
        return super.jdbcTemplate.queryForObject(
                "select skillId from skills where skillName = 'test1'", Long.class);
    }
    private long idTestUser1(){
        return super.jdbcTemplate.queryForObject(
                "select id from users where username = 'test1'", Long.class);
    }
    private long idTestUser2(){
        return super.jdbcTemplate.queryForObject(
                "select id from users where username = 'test2'", Long.class);
    }
    private long idTestUser3(){
        return super.jdbcTemplate.queryForObject(
                "select id from users where username = 'test3'", Long.class);
    }
}
