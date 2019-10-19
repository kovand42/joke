package net.kovand42.kova_design.web;

import net.kovand42.kova_design.entities.*;
import net.kovand42.kova_design.forms.ProjectForm;
import net.kovand42.kova_design.forms.ProjectMessageForm;
import net.kovand42.kova_design.forms.RepositoryForm;
import net.kovand42.kova_design.services.*;
import net.kovand42.kova_design.sessions.ProjectSkills;
import net.kovand42.kova_design.sessions.SkillsForNewProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/projects")
public class ProjectController {
    @Autowired
    ControllerFunctions controllerFunctions;
    @Autowired
    ProjectService projectService;
    @Autowired
    UserSkillService userSkillService;
    @Autowired
    SkillService skillService;
    @Autowired
    UserService userService;
    @Autowired
    SkillsForNewProject skillsForNewProject;
    @Autowired
    ProjectSkills projectSkills;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    ProjectMessageService projectMessageService;
    @Autowired
    ProjectAuthorityService projectAuthorityService;
    @GetMapping
    ModelAndView projects(Principal principal){
        projectSkills.setClear();
        ModelAndView modelAndView = new ModelAndView("projects")
                .addObject("projects", projectService.findAll())
                .addObject("principal", principal);
        return modelAndView;
    }
    @GetMapping("{id}")
    ModelAndView project(@PathVariable long id, Principal principal){
        ModelAndView modelAndView = new ModelAndView("project");
        Project project = projectService.findById(id).get();
        List<User> users = controllerFunctions.makeProjectUserList(project);
        User user = userService.findByUsername(principal.getName()).get();
        boolean cont = users.contains(user)||controllerFunctions.projectUserAuth(project, user).equals("admin");
        modelAndView.addObject("principal", principal)
                .addObject("user", user)
                .addObject("project", project)
                .addObject("users", users)
                .addObject("projectAuthority", controllerFunctions.projectUserAuth(project, user))
                .addObject("cont", cont)
                .addObject("projectSkills", controllerFunctions.makeProjectSkills(project))
                .addObject("lackingSkills", controllerFunctions.lackingProjectSkills(project))
                .addObject("lackingUserSkills", controllerFunctions.lackingUserSkills(project))
                .addObject("usersWithLackingProjectSkill", controllerFunctions.makeLackingSkillsUserList(project))
                .addObject("messages", controllerFunctions.messages(project))
                .addObject("messagesMap", controllerFunctions.userMessages(project, user))
                .addObject("messageForm",
                new ProjectMessageForm(project, userService.
                        findByUsername(principal.getName()).get(), null));
        return modelAndView;
    }
    @GetMapping("newProject")
    ModelAndView newProject(Principal principal){
        ModelAndView modelAndView = new ModelAndView("newProject");
        User user = userService.findByUsername(principal.getName()).get();
        List<Skill> principalSkills = controllerFunctions.makeSkillListFromUserForProject(user);
        List<Skill> skillsInUse = new LinkedList<>();
        skillsForNewProject.getNewSkills().stream().forEach(id ->
                skillsInUse.add(skillService.findById(id).get()));
        modelAndView.addObject("empty", skillsInUse.isEmpty());
        modelAndView.addObject("lackingSkills", controllerFunctions.lackingSkills(user));
        modelAndView.addObject("skillsInUse", skillsInUse);
        modelAndView.addObject("user", user);
        modelAndView.addObject("skills", principalSkills);
        modelAndView.addObject("projectForm", new ProjectForm(null, null));
        modelAndView.addObject("repositoryForm", new RepositoryForm(null, null));
        return modelAndView;
    }
    @GetMapping("newProjectSkills")
    ModelAndView newProjectSkills(@RequestParam("id") long id, RedirectAttributes redirect){
        skillsForNewProject.add(id);
        return new ModelAndView("redirect:/projects/newProject");
    }
    @GetMapping("projectSkills")
    ModelAndView projectSkills(@RequestParam("id") long id,
                                   @RequestParam("projectId") long projectId,
                                   RedirectAttributes redirect){
        controllerFunctions.addSkillToProject(projectService.findById(projectId).get(), id);
        return new ModelAndView(controllerFunctions.redirectToProjectWithId(projectId));
    }
    @GetMapping("projectSkills/remove")
    ModelAndView removeSkill(@RequestParam("id") long id,
                             @RequestParam("projectId") long projectId,
                             RedirectAttributes redirect){
        controllerFunctions.removeSkillFromProject(projectService.findById(projectId).get(), id);
        return new ModelAndView(controllerFunctions.redirectToProjectWithId(projectId));
    }
    @GetMapping("newProjectSkills/remove")
    ModelAndView remove(@RequestParam("id") long id, RedirectAttributes redirect){
        skillsForNewProject.remove(id);
        return new ModelAndView("redirect:/projects/newProject");
    }
    @PostMapping("create/repo")
    ModelAndView ceateRepo(@Valid RepositoryForm repositoryForm,
            Errors errors, @RequestParam("projectName") String projectName,
                           @RequestParam("url") String url,
                           RedirectAttributes redirect, Principal principal){
        ModelAndView modelAndView = new ModelAndView("createProject");
        if(errors.hasErrors()){
            return modelAndView;
        }
        Repository repository = new Repository(repositoryForm.getRepositoryName(), url);
        repositoryService.create(repository);
        controllerFunctions.makeProjectFromUrlAndProjectName(repositoryForm.getUrl(), projectName, principal);
        return new ModelAndView("redirect:/projects");
    }
    @PostMapping("create")
    ModelAndView create(@Valid ProjectForm projectForm, Errors appErrors,
                        @Valid RepositoryForm repositoryForm, Errors repoErrors,
                        RedirectAttributes redirect, Principal principal){
        if(repositoryService.findByUrl(repositoryForm.getUrl()).isPresent()){
            controllerFunctions.makeProjectFromUrlAndProjectName(repositoryForm.getUrl(), projectForm.getProjectName(), principal);
            return new ModelAndView("redirect:/projects");
        }
        ModelAndView modelAndView = new ModelAndView("createProject")
                .addObject("url", repositoryForm.getUrl())
                .addObject("projectName", projectForm.getProjectName())
                .addObject("repoForm", new RepositoryForm(null, repositoryForm.getUrl()));
       return modelAndView;
    }
    @PostMapping("postMessage")
    ModelAndView postMessage(@Valid ProjectMessageForm projectMessageForm, Errors errors,
                             @RequestParam("id") long id,
                             @RequestParam("projectId") long projectId,
                             RedirectAttributes redirect){
        StringBuilder strB = new StringBuilder();
        strB.append("redirect:/projects/").append(projectId);
        if(errors.hasErrors()){
            errors.getFieldErrors().forEach(fieldError -> System.out.println(fieldError.toString()));
            return new ModelAndView(strB.toString());
        }
        User user = userService.findById(id).get();
        Project project = projectService.findById(projectId).get();
        String message = projectMessageForm.getMessage();
        LocalDateTime dateTime = LocalDateTime.now();
        ProjectMessage projectMessage = new ProjectMessage(project, user, dateTime, message);
        projectMessageService.create(projectMessage);
        return new ModelAndView(strB.toString());
    }
}
