package net.kovand42.kova_design.web;

import net.kovand42.kova_design.entities.*;
import net.kovand42.kova_design.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;
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
        User principalUser = userService.findByUsername(principal.getName()).get();
        List<Project> projects = controllerFunctions.makeProjectListFromUser(user);
        List<Skill> skills = controllerFunctions.makeSkillListFromUserForProfile(user);
        Set<Request> requests = controllerFunctions.makeRequestSet(projects, user);
        List<ProjectAuthority> projectAuthorities = controllerFunctions.makeProjectAuthorities(projects);
        List<Project> principalProjects = controllerFunctions.makeProjectListFromUser(principalUser);
        List<ProjectAuthority> principalProjectAuthorities = controllerFunctions.makeProjectAuthorities(principalProjects);
        List<Project> userProjectsWithAdminAuth = controllerFunctions.makeProjectListWithAdminAuth(projectAuthorities, user);
        List<Project> principalProjectsWithAdminAuth = controllerFunctions.makeProjectListWithAdminAuth(principalProjectAuthorities, principalUser);
        Map<Request, Boolean> projectInvitMap = controllerFunctions
                .makeInvitationRequestsMap(requests, principalProjectsWithAdminAuth);
        Map<Request, Boolean> projectApplyMap = controllerFunctions
                .makeApplyRequestsMap(requests, principalProjectsWithAdminAuth);
        ModelAndView modelAndView = new ModelAndView("profile")
                .addObject("user", userService.findById(id).get());
        modelAndView.addObject("projectInvitMapSize", projectInvitMap.size());
        modelAndView.addObject("projectInvitMap", projectInvitMap);
        modelAndView.addObject("projectApplyMapSize", projectApplyMap.size());
        modelAndView.addObject("projectApplyMap", projectApplyMap);
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
    @PostMapping("/makeRequest")
    ModelAndView makeRequest(@RequestParam("id") long id,
                             @RequestParam("projectId") long projectId,
                             @RequestParam("invitation") boolean invitation,
                             RedirectAttributes redirect){
        Project project = projectService.findById(projectId).get();
        User user = userService.findById(id).get();
        requestService.create(new Request(project, user, invitation));
        StringBuilder strB = new StringBuilder();
        strB.append("redirect:/profile/").append(id);
        String URI = strB.toString();
        return new ModelAndView(URI);
    }
    @PostMapping("/deleteRequest")
        ModelAndView deleteRequest(@RequestParam("id") long id,
                                   @RequestParam("projectId") long projectId,
                                   RedirectAttributes redirect, Principal principal){
            Project project = projectService.findById(projectId).get();
            User user = userService.findById(id).get();
        controllerFunctions.deleteRequest(project, user);
            //requestService.delete(requestService.findByProjectAndUser(project, user).get(0));
        long principalId = userService.findByUsername(principal.getName()).get().getId();
        StringBuilder strB = new StringBuilder();
        strB.append("redirect:/profile/").append(principalId);
        String URI = strB.toString();
        return new ModelAndView(URI);
    }
    @PostMapping("/addProjects/add")
    ModelAndView saveAddProject(@RequestParam("id") long id,
                                    @RequestParam("projectId") long projectId,
                                    RedirectAttributes redirect, Principal principal){
        Project project = projectService.findById(projectId).get();
        System.out.println(project.getProjectName());
        User user = userService.findById(id).get();
        System.out.println(user.getUsername());
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
        controllerFunctions.deleteRequest(project, user);
        //requestService.delete(requestService.findByProjectAndUser(project, user).get(0));
        long principalId = userService.findByUsername(principal.getName()).get().getId();
        StringBuilder strB = new StringBuilder();
        strB.append("redirect:/profile/").append(principalId);
        String URI = strB.toString();
        return new ModelAndView(URI);
    }
    @PostMapping("/removeProject")
    ModelAndView removeProject(@RequestParam("projectId") long projectId,
                                   @RequestParam("id") long id, RedirectAttributes redirect){
        User user = userService.findById(id).get();
        List<UserSkill> userSkills = userSkillService.findByUser(user);
        Project project = projectService.findById(projectId).get();
        ProjectAuthority projectAuthority = projectAuthorityService
                .findByProjectAndAuthority(project, "admin").get();
        AtomicInteger projectUsers = new AtomicInteger(0);
        project.getUserSkills().stream().forEach(userSkill -> {
            if(!(userSkill.getUser().equals(user))
                    &&!(userSkill.getUser().getUsername().equals("master"))){
                projectUsers.getAndIncrement();
            }
        });
        if(projectUsers.get()>0){
            if(user.getProjectAuthorities().contains(projectAuthority)){
                List<User> users = controllerFunctions.makeProjectUserList(project);
                users.remove(user);
                User nambatooTemporaire = users.get(0);
                User nambatoo = userService.findByUsername(nambatooTemporaire.getUsername()).get();
                ProjectAuthority projectAuthority1 = projectAuthorityService.findByProjectAndAuthority(project, "user").get();
                projectAuthority1.removeUser(nambatoo);
                nambatoo.removeProjectAuthority(projectAuthority1);
                user.removeProjectAuthority(projectAuthority);
                nambatoo.addProjectAuthority(projectAuthority);
                userSkills.forEach(userSkill -> {
                    project.remove(userSkill);
                });
                userService.update(user);
                userService.update(nambatoo);
                projectAuthorityService.update(projectAuthority);
                projectAuthorityService.update(projectAuthority1);
            }else {
                ProjectAuthority projectAuthority1 = projectAuthorityService.findByProjectAndAuthority(project, "user").get();
                user.removeProjectAuthority(projectAuthority1);
                projectAuthority1.removeUser(user);
                userService.update(user);
                projectAuthorityService.update(projectAuthority1);
            }
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
        Project project = projectService.findById(projectId).get();
        ProjectAuthority projectAuthority = projectAuthorityService.findByProjectAndAuthority(project, "admin").get();
        User user = userService.findById(id).get();
        projectAuthority.removeUser(user);
        user.removeProjectAuthority(projectAuthority);
        projectAuthorityService.update(projectAuthority);
        userService.update(user);
        controllerFunctions.deleteProject(project);
        return new ModelAndView(controllerFunctions.redirectToProfileAfterDeleteProject(id));
    }
}
