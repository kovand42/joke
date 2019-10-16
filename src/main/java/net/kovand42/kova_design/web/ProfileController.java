package net.kovand42.kova_design.web;

import net.kovand42.kova_design.entities.Project;
import net.kovand42.kova_design.entities.Skill;
import net.kovand42.kova_design.entities.User;
import net.kovand42.kova_design.entities.UserSkill;
import net.kovand42.kova_design.services.ProjectService;
import net.kovand42.kova_design.services.SkillService;
import net.kovand42.kova_design.services.UserService;
import net.kovand42.kova_design.services.UserSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
    ProjectService projectService;
    @Autowired
    SkillService skillService;
    @GetMapping("/{id}")
    ModelAndView profile(@PathVariable long id, Principal principal){
        User user = userService.findById(id).get();
        List<Project> projects = makeProjectListFromUser(user);
        List<Skill> skills = makeSkillListFromUser(user);
        ModelAndView modelAndView = new ModelAndView("profile")
                .addObject("user", userService.findById(id).get());
        modelAndView.addObject("skills", skills);
        modelAndView.addObject("projects", projects);
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
    @GetMapping("/addProjects")
    ModelAndView addProjects(@RequestParam("id") long id){
        ModelAndView modelAndView = new ModelAndView("addProjects");
        User user = userService.findById(id).get();
        List<Project> newProjects = makeNewProjectListForUser(user);
        modelAndView.addObject("user", user).addObject("id", id);
        return modelAndView.addObject("projects", newProjects);
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
    @PostMapping("/addProjects/add")
    ModelAndView saveAddApplication(@RequestParam("id") long id,
                                    @RequestParam("projectId") long projectId,
                                    RedirectAttributes redirect){
        Project project = projectService.findById(projectId).get();
        User user = userService.findById(id).get();
        Set<UserSkill> projectUserSkills = project.getUserSkills();
        List<UserSkill> userSkills = userSkillService.findByUser(user);
        Set<UserSkill> userSkillsInUse = new LinkedHashSet<>();
        projectUserSkills.stream().forEach(userSkill -> {
            userSkills.forEach(userSkill1 -> {
                if(userSkill1.getSkill().equals(userSkill.getSkill())){
                    userSkillsInUse.add(userSkill1);
                }
            });
        });
        userSkillsInUse.stream().forEach(userSkill -> {
            project.add(userSkill);
        });
        projectService.update(project);
        redirect.addAttribute("id", id);
        return new ModelAndView("redirect:/profile/addProjects");
    }
    @PostMapping("/removeProject")
    ModelAndView removeProject(@RequestParam("projectId") long projectId,
                                   @RequestParam("id") long id, RedirectAttributes redirect){
        User user = userService.findById(id).get();
        List<UserSkill> userSkills = userSkillService.findByUser(user);
        Project project = projectService.findById(projectId).get();
        AtomicInteger projectUsers = new AtomicInteger(0);
        project.getUserSkills().stream().forEach(userSkill -> {
            if(!(userSkill.getUser().equals(user))
                    &&!(userSkill.getUser().getUsername().equals("master"))){
                projectUsers.getAndIncrement();
                System.out.println(projectUsers.get());
                System.out.println(project.getProjectName());
            }
        });
        if(projectUsers.get()>0){
            userSkills.forEach(userSkill -> {
                project.remove(userSkill);
            });
            projectService.update(project);
            redirect.addAttribute("id", id);
            return new ModelAndView("redirect:/profile/addProjects");
        }
        ModelAndView modelAndView = new ModelAndView("deleteProject");
        modelAndView.addObject("user", user);
        modelAndView.addObject("project", project);
        return modelAndView;
    }
    @PostMapping("/deleteProject")
    ModelAndView deleteProject(@RequestParam("id") long id,
                                   @RequestParam("projectId") long projectId,
                                   RedirectAttributes redirect){
        redirect.addAttribute("id", id);
        Project project = projectService.findById(projectId).get();
        projectService.delete(project);
        StringBuilder strb = new StringBuilder();
        strb.append("redirect:/profile/").append(id);
        return new ModelAndView(strb.toString());
    }
    private List<Skill> makeSkillListFromUser(User user){
        List<UserSkill> userSkills = userSkillService.findByUser(user);
        List<Skill> skills = new LinkedList<>();
        userSkills.forEach(userSkill -> {
            skills.add(userSkill.getSkill());
        });
        return skills;
    }
    private List<Project> makeProjectListFromUser(User user){
        List<UserSkill> userSkills = userSkillService.findByUser(user);
        List<Project> projects = new LinkedList<>();
        userSkills.forEach(userSkill -> {
            userSkill.getProjects().stream().forEach(project -> {
                if(!projects.contains(project)) {
                    projects.add(project);
                }
            });
        });
        return projects;
    }
    private List<Project> makeNewProjectListForUser(User user){
        List<Project> userProjects = makeProjectListFromUser(user);
        List<Project> projects = projectService.findAll();
        List<Project> newProjectList = new LinkedList<>();
        List<Skill> userSkills = makeSkillListFromUser(user);
        projects.removeAll(userProjects);
        projects.forEach(application -> {
            application.getUserSkills().stream().forEach(userSkill -> {
                if(userSkills.contains(userSkill.getSkill())
                &&!newProjectList.contains(application)
                &&!userProjects.contains(application)){
                    newProjectList.add(application);
                }
            });
        });
        return newProjectList;
    }
}
