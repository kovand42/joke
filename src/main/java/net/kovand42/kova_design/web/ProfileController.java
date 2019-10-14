package net.kovand42.kova_design.web;

import net.kovand42.kova_design.entities.Application;
import net.kovand42.kova_design.entities.Skill;
import net.kovand42.kova_design.entities.User;
import net.kovand42.kova_design.entities.UserSkill;
import net.kovand42.kova_design.services.ApplicationService;
import net.kovand42.kova_design.services.SkillService;
import net.kovand42.kova_design.services.UserService;
import net.kovand42.kova_design.services.UserSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("/profile")
@SessionAttributes("user")
public class ProfileController {
    @Autowired
    UserService userService;
    @Autowired
    UserSkillService userSkillService;
    @Autowired
    ApplicationService applicationService;
    @Autowired
    SkillService skillService;
    @GetMapping("/{id}")
    ModelAndView profile(@PathVariable long id, Principal principal){
        User user = userService.findById(id).get();
        List<Application> applications = makeAppListFromUser(user);
        List<Skill> skills = makeSkillListFromUser(user);
        ModelAndView modelAndView = new ModelAndView("profile")
                .addObject("user", userService.findById(id).get());
        modelAndView.addObject("skills", skills);
        modelAndView.addObject("applications", applications);
        modelAndView.addObject("principal", principal.getName());
        return modelAndView;
    }
    @GetMapping("/addSkills")
    ModelAndView addSkills(@RequestParam("id") long id){
        ModelAndView modelAndView = new ModelAndView("addSkills");
        List<Skill> skills1 = skillService.findAll();
        List<Skill> skills = new LinkedList<>();
        User user = userService.findById(id).get();
        List<Skill> skillsFromUser = makeSkillListFromUser(user);
        skills1.forEach(skill1 -> {
            if(!skillsFromUser.contains(skill1)){
                skills.add(skill1);
            }
        });
        modelAndView.addObject("user", user).addObject("id", id);
        return modelAndView.addObject("skills", skills);
    }
    @GetMapping("/addApplications")
    ModelAndView addApplications(@RequestParam("id") long id){
        ModelAndView modelAndView = new ModelAndView("addApplications");
        User user = userService.findById(id).get();
        List<Application> newApplications = makeNewAppListForUser(user);
        modelAndView.addObject("user", user).addObject("id", id);
        return modelAndView.addObject("applications", newApplications);
    }
    @PostMapping("/addSkills/add")
    ModelAndView saveAddSkill(@RequestParam("id") long id,
                              @RequestParam("skillId") long skillId,
                              RedirectAttributes redirect){
        Skill skill = skillService.findById(skillId).get();
        User user = userService.findById(id).get();
        UserSkill userSkill = new UserSkill(user, skill, false);
        userSkillService.create(userSkill);
        redirect.addAttribute("id", id);
        return new ModelAndView("redirect:/profile/addSkills");
    }
    @PostMapping("/addApplications/add")
    ModelAndView saveAddApplication(@RequestParam("id") long id,
                                    @RequestParam("applicationId") long applicationId,
                                    RedirectAttributes redirect){
        Application application = applicationService.findById(applicationId).get();
        User user = userService.findById(id).get();
        Set<UserSkill> appUserSkills = application.getUserSkills();
        List<UserSkill> userSkills = userSkillService.findByUser(user);
        Set<UserSkill> userSkillsInUse = new LinkedHashSet<>();
        appUserSkills.stream().forEach(userSkill -> {
            userSkills.forEach(userSkill1 -> {
                if(userSkill1.getSkill().equals(userSkill.getSkill())){
                    userSkillsInUse.add(userSkill1);
                }
            });
        });
        userSkillsInUse.stream().forEach(userSkill -> {
            application.add(userSkill);
        });
        applicationService.update(application);
        redirect.addAttribute("id", id);
        return new ModelAndView("redirect:/profile/addApplications");
    }
    @PostMapping("/removeApplication")
    ModelAndView removeApplication(@RequestParam("applicationId") long applicationId,
                                   @RequestParam("id") long id, RedirectAttributes redirect){
        User user = userService.findById(id).get();
        List<UserSkill> userSkills = userSkillService.findByUser(user);
        Application application = applicationService.findById(applicationId).get();
        AtomicInteger appUsers = new AtomicInteger(0);
        application.getUserSkills().stream().forEach(userSkill -> {
            if(!userSkill.getUser().equals(user)){
                appUsers.getAndIncrement();
                System.out.println(appUsers.get());
                System.out.println(application.getApplicationName());
            }
        });
        if(appUsers.get()>0){
            userSkills.forEach(userSkill -> {
                application.remove(userSkill);
            });
            applicationService.update(application);
            redirect.addAttribute("id", id);
            return new ModelAndView("redirect:/profile/addApplications");
        }
        ModelAndView modelAndView = new ModelAndView("deleteApplication");
        modelAndView.addObject("user", user);
        modelAndView.addObject("app", application);
        return modelAndView;
    }
    @PostMapping("/deleteApplication")
    ModelAndView deleteApplication(@RequestParam("id") long id,
                                   @RequestParam("applicationId") long applicationId,
                                   RedirectAttributes redirect){
        redirect.addAttribute("id", id);
        System.out.println(applicationService.findAll().size());
        Application application = applicationService.findById(applicationId).get();
        applicationService.delete(application);
        System.out.println(applicationService.findAll().size());
        return new ModelAndView("redirect:/profile/addApplications");
    }
    private List<Skill> makeSkillListFromUser(User user){
        List<UserSkill> userSkills = userSkillService.findByUser(user);
        List<Skill> skills = new LinkedList<>();
        userSkills.forEach(userSkill -> {
            skills.add(userSkill.getSkill());
        });
        return skills;
    }
    private List<Application> makeAppListFromUser(User user){
        List<UserSkill> userSkills = userSkillService.findByUser(user);
        List<Application> applications = new LinkedList<>();
        userSkills.forEach(userSkill -> {
            userSkill.getApplications().stream().forEach(application -> {
                if(!applications.contains(application)) {
                    applications.add(application);
                }
            });
        });
        return applications;
    }
    private List<Application> makeNewAppListForUser(User user){
        List<Application> userApplications = makeAppListFromUser(user);
        List<Application> applications = applicationService.findAll();
        List<Application> newAppList = new LinkedList<>();
        List<Skill> userSkills = makeSkillListFromUser(user);
        applications.removeAll(userApplications);
        applications.forEach(application -> {
            application.getUserSkills().stream().forEach(userSkill -> {
                if(userSkills.contains(userSkill.getSkill())
                &&!newAppList.contains(application)
                &&!userApplications.contains(application)){
                    newAppList.add(application);
                }
            });
        });
        return newAppList;
    }
}
