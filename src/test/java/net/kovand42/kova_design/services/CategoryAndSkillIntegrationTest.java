package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Category;
import net.kovand42.kova_design.entities.Skill;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("/categories.sql")
@Sql("/skills.sql")
public class CategoryAndSkillIntegrationTest
        extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    CategoryService categoryService;
    @Autowired
    SkillService skillService;
    private Skill skill;
    private Category category;
    @Before
    public void before(){
        this.skill = new Skill("test1", categoryService.findById(idTestCategory2()).get(), false);
        this.category = new Category("test1");
    }
    @Test
    public void skill1vAndSkill2HaveTheSameCategory(){
        assertTrue(skillService.findById(idTestSkill1v()).get().getCategory()
                .equals(skillService.findById(idTestSkill2()).get().getCategory()));
    }
    @Test
    public void skill1AndSkill1vAndSkill1AndSkill2HaveNotTheSemaCategory(){
        assertFalse(skillService.findById(idTestSkill1()).get().getCategory()
                .equals(skillService.findById(idTestSkill1v()).get().getCategory()));
        assertFalse(skillService.findById(idTestSkill1()).get().getCategory()
                .equals(skillService.findById(idTestSkill2()).get().getCategory()));
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void skillNameHasToBeUnique(){
        skillService.create(skill);
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void categoryNameHasToBeUnique(){
        categoryService.create(category);
    }
    @Test
    public void findByCategoryReturnsSkillsBelongingToGivenCategory(){
        assertEquals(skillService.findByCategory(
                categoryService.findById(idTestCategory2()).get()).size(), 2);
    }

    private long idTestCategory2(){
        return super.jdbcTemplate.queryForObject(
                "select categoryId from categories where categoryName = 'test2'", Long.class);
    }
    private long idTestSkill1(){
        return super.jdbcTemplate.queryForObject(
                "select skillId from skills where skillName = 'test1'", Long.class);
    }
    private long idTestSkill1v(){
        return super.jdbcTemplate.queryForObject(
                "select skillId from skills where skillName = 'test1v'", Long.class);
    }
    private long idTestSkill2(){
        return super.jdbcTemplate.queryForObject(
                "select skillId from skills where skillName = 'test2'", Long.class);
    }
}
