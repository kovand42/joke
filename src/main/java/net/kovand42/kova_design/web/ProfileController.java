package net.kovand42.kova_design.web;

import net.kovand42.kova_design.entities.*;
import net.kovand42.kova_design.services.*;
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
    ControllerFunctions controllerFunctions;
    @Autowired
    UserService userService;
    @Autowired
    UserSkillService userSkillService;
    @Autowired
    ProjectService projectService;
    @Autowired
    SkillService skillService;
    @Autowired
    RequestService requestService;
    @Autowired
    ProjectAuthorityService projectAuthorityService;
    @GetMapping("/{id}")
    ModelAndView profile(@PathVariable long id, Principal principal){
        User user = userService.findById(id).get();
        List<Project> projects = controllerFunctions.makeProjectListFromUser(user);
        List<Skill> skills = controllerFunctions.makeSkillListFromUserForProfile(user);
        List<Request> requests = new LinkedList<>();
        List<ProjectAuthority> projectAuthorities = new LinkedList<>();
        projects.forEach(project -> {
            requests.addAll(requestService.findByProjectAndUser(project, user));
            projectAuthorities.addAll(projectAuthorityService.findAllByProject(project));
        });
        List<Project> userProjectsWithAdminAuth = new LinkedList<>();
        projectAuthorities.forEach(projectAuthority -> {
            if(projectAuthority.getUsersWithAuth().contains(user)&&
                    projectAuthority.getProjectAuthority().equals("admin")){
                userProjectsWithAdminAuth.add(projectAuthority.getProject());
            }
        });
        System.out.println(userProjectsWithAdminAuth.size());
        userProjectsWithAdminAuth.forEach(project -> {
            requests.addAll(requestService.findByProject(project));
        });
        System.out.println(requests.size());
        ModelAndView modelAndView = new ModelAndView("profile")
                .addObject("user", userService.findById(id).get());
        modelAndView.addObject("requests", requests);
        modelAndView.addObject("requests", requests);
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
        List<Skill> skillsFromUser = controllerFunctions.makeSkillListFromUserForProfile(user);
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
        List<Project> newProjects = controllerFunctions.makeNewProjectListForUser(user);
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
    ModelAndView saveAddProject(@RequestParam("id") long id,
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
        requestService.create(new Request(project, user, false));
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
        /*redirect.addAttribute("id", id);*/
        Project project = projectService.findById(projectId).get();
        controllerFunctions.deleteProject(project);
        //projectService.delete(project);
/*        StringBuilder strb = new StringBuilder();
        strb.append("redirect:/profile/").append(id);*/
        return new ModelAndView(controllerFunctions.redirectToProfileAfterDeleteProject(id));
    }
}
