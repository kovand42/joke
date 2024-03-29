package net.kovand42.kova_design.web;

import net.kovand42.kova_design.entities.*;
import net.kovand42.kova_design.services.CategoryService;
import net.kovand42.kova_design.services.SkillService;
import net.kovand42.kova_design.services.UserService;
import net.kovand42.kova_design.services.UserSkillService;
import net.kovand42.kova_design.sessions.Open;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/skills")
@SessionAttributes("open")
public class SkilllController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    SkillService skillService;
    @Autowired
    UserSkillService userSkillService;
    @Autowired
    UserService userService;
    @Autowired
    Open open;
    @GetMapping
    ModelAndView skills(){
        ModelAndView modelAndView = decomposeOpen();
        return modelAndView.addObject("categoryList", categoryService.findAll());
    }
    @GetMapping("/categories/{id}")
    ModelAndView category(@PathVariable long id){
        open.add(id);
        ModelAndView modelAndView = decomposeOpen();
        Category category = categoryService.findById(id).get();
        modelAndView.addObject("categoryList", categoryService.findAll());
        modelAndView.addObject("category",category);
        modelAndView.addObject("skillList", skillService.findByCategory(category));
        return modelAndView;

    }

    @GetMapping("/categories/removeCategory/{id}")
    public ModelAndView removeSkill(@PathVariable long id, RedirectAttributes redirect) {
        open.remove(id);
        return new ModelAndView("redirect:/skills");
    }

    @GetMapping("{id}")
    ModelAndView skill(@PathVariable long id){
        ModelAndView modelAndView = new ModelAndView("skill");
        Skill skill = skillService.findById(id).get();
        List<UserSkill> userSkills = userSkillService.findBySkill(skill);
        modelAndView.addObject("skill", skill);
        modelAndView.addObject("projects",makeProjectSet(userSkills));
        modelAndView.addObject("users", makeUserSet(skill));
        return modelAndView;
    }

    private Set<User> makeUserSet(Skill skill){
        Set<User> users = new LinkedHashSet<>();
        List<UserSkill> userSkills = userSkillService.findBySkill(skill);
        userSkills.forEach(userSkill -> {
            if (!users.contains(userSkill.getUser())){
                users.add(userSkill.getUser());
            }
        });
        return users;
    }

    private Set<Project> makeProjectSet(List<UserSkill> userSkills){
        Set<Project> projects = new LinkedHashSet<>();
        userSkills.forEach(userSkill -> {
            userSkill.getProjects().stream().forEach(project -> {
                if(!projects.contains(project)){
                    projects.add(project);
                }
            });
        });
        return projects;
    }

    private ModelAndView decomposeOpen() {
        ModelAndView modelAndView = new ModelAndView("skills");
        Set<Category> activeCategorySet = new LinkedHashSet<>();
        Map<Skill, Long> activeSkillMap = new LinkedHashMap<>();
        open.getOpen().forEach(id -> {
            Category category = categoryService.findById(id).get();
            activeCategorySet.add(category);
            skillService.findByCategory(category).forEach(
                    skill -> activeSkillMap.put(skill, id));
        });
        modelAndView.addObject("activeSkillMap", activeSkillMap);
        modelAndView.addObject("activeCategorySet", activeCategorySet);
        return modelAndView;
    }
}
