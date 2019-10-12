package net.kovand42.kova_design.web;

import net.kovand42.kova_design.entities.Application;
import net.kovand42.kova_design.entities.Skill;
import net.kovand42.kova_design.entities.User;
import net.kovand42.kova_design.entities.UserSkill;
import net.kovand42.kova_design.services.SkillService;
import net.kovand42.kova_design.services.UserService;
import net.kovand42.kova_design.services.UserSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/skills")
public class SkilllController {
    @Autowired
    SkillService skillService;
    @Autowired
    UserSkillService userSkillService;
    @Autowired
    UserService userService;
    @GetMapping
    ModelAndView skills(){
        return new ModelAndView("skills", "skills",skillService.findAll());
    }
    @GetMapping("/{id}")
    ModelAndView skill(@PathVariable long id){
        ModelAndView modelAndView = new ModelAndView("skill");
        Skill skill = skillService.findById(id).get();
        List<UserSkill> userSkills = userSkillService.findBySkill(skill);
        modelAndView.addObject("skill", skill);
        modelAndView.addObject("applications",makeApplicationSet(userSkills));
        return modelAndView;
    }

    public static Set<User> makeUserSet(List<UserSkill> userSkills){
        Set<User> users = new LinkedHashSet<>();
        userSkills.forEach(userSkill -> {
            if(!users.contains(userSkill.getUser())){
                users.add(userSkill.getUser());
            }
        });
        return users;
    }

    public static Set<Application> makeApplicationSet(List<UserSkill> userSkills){
        Set<Application> applications = new LinkedHashSet<>();
        userSkills.forEach(userSkill -> {
            userSkill.getApplications().stream().forEach(application -> {
                if(!applications.contains(application)){
                    applications.add(application);
                }
            });
        });
        return applications;
    }
}
