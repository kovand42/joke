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

import javax.servlet.http.HttpSession;
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
    @Autowired
    RequestService requestService;
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
        List<Request> requests = requestService.findByProject(project);
        List<Request> invitationRequests = new LinkedList<>();
        List<Request> notInvitationRequests = new LinkedList<>();
        List<User> invitedUsers = new LinkedList<>();
        requestService.findByProject(project).forEach(request -> {
            if(request.isInvitation()){
                invitationRequests.add(request);
                invitedUsers.add(request.getUser());
            }else {
                notInvitationRequests.add(request);
            }
        });
        List<User> usersAlreadyApplied = new LinkedList<>();
        notInvitationRequests.forEach(request -> {
            usersAlreadyApplied.add(request.getUser());
        });
        boolean isUserInvited = false;
        if(invitedUsers.contains(user)||users.contains(user)||usersAlreadyApplied.contains(user)){
            isUserInvited = true;
        }
        List<User> notInvitedUsersWithLackingSkills = controllerFunctions.makeLackingSkillsUserList(project);
        notInvitedUsersWithLackingSkills.removeAll(invitedUsers);
        modelAndView.addObject("principal", principal)
                .addObject("requests", notInvitationRequests)
                .addObject("isUserInvited", isUserInvited)
                .addObject("invitationRequests", invitationRequests)
                .addObject("user", user)
                .addObject("project", project)
                .addObject("users", users)
                .addObject("projectAuthority", controllerFunctions.projectUserAuth(project, user))
                .addObject("cont", cont)
                .addObject("projectSkills", controllerFunctions.makeProjectSkills(project))
                .addObject("lackingSkills", controllerFunctions.lackingProjectSkills(project))
                .addObject("lackingUserSkills", controllerFunctions.lackingUserSkills(project))
                .addObject("usersWithLackingProjectSkill", notInvitedUsersWithLackingSkills)
                .addObject("messages", controllerFunctions.messages(project))
                .addObject("messagesMap", controllerFunctions.userMessages(project, user))
                .addObject("messageForm",
                new ProjectMessageForm(project, userService.
                        findByUsername(principal.getName()).get(), null));
        return modelAndView;
    }
    @GetMapping("/newProject")
    ModelAndView newProject(Principal principal, HttpSession session){
        ModelAndView modelAndView = new ModelAndView("newProject");
        User user = userService.findByUsername(principal.getName()).get();
        List<Skill> principalSkills = controllerFunctions.makeSkillListFromUserForProject(user);
        List<Skill> skillsInUse = new LinkedList<>();
        skillsForNewProject.getNewSkills().stream().forEach(id ->
                skillsInUse.add(skillService.findById(id).get()));
        if(session.getAttribute("mess") != null){
            modelAndView.addObject("mess", session.getAttribute("message"));
        }else{
            modelAndView.addObject("mess",null);
        }
        modelAndView.addObject("skillsInUseIsempty", skillsInUse.isEmpty());
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
            Errors repoErrors, @RequestParam("projectName") String projectName,
                           RedirectAttributes redirect, Principal principal, HttpSession session){
        if(projectName.trim().equals(null)){
            session.setAttribute("mess", "projectNameIsEmpty");
            return new ModelAndView("redirect:/projects/newProject");
        }
        if(repoErrors.hasErrors()){
            System.out.println("errors: " + repoErrors.getFieldErrors().get(0));
            StringBuilder urlToTemplate = new StringBuilder();
            if(repositoryForm.getUrl()== null){
                urlToTemplate = null;
            }else{
                urlToTemplate.append(repositoryForm.getUrl());
            }
            StringBuilder repoNameToTemplate = new StringBuilder();
            if(repositoryForm.getRepositoryName()==null){
                repoNameToTemplate = null;
            }else {
                repoNameToTemplate.append(repositoryForm.getRepositoryName());
            }
            String strUrl = urlToTemplate.toString();
            String strRepo = repoNameToTemplate.toString();
            return new ModelAndView("createProject")
                    .addObject("projectName", projectName)
                    .addObject(new RepositoryForm(strRepo, strUrl)).addObject("errors", repoErrors);
        }
        System.out.println("no errors");
        Repository repository = new Repository(repositoryForm.getRepositoryName(), repositoryForm.getUrl());
        repositoryService.create(repository);
        controllerFunctions.makeProjectFromUrlAndProjectName(repositoryForm.getUrl(), projectName, principal);
        return new ModelAndView("redirect:/projects");
    }
    @PostMapping("/create")
    ModelAndView create(@Valid ProjectForm projectForm, Errors projectErrors,
                        @Valid RepositoryForm repositoryForm, Errors repoErrors,
                        RedirectAttributes redirect, Principal principal, HttpSession session){
        if(!(projectForm.getProjectName().trim().length()>0)){
            session.setAttribute("mess", "projectNameIsEmpty");
            return new ModelAndView("redirect:/projects/newProject");
        }
        ModelAndView modelAndView = new ModelAndView("createProject");
        if(repositoryService.findByUrl(repositoryForm.getUrl()).isPresent()){
            controllerFunctions.makeProjectFromUrlAndProjectName(repositoryForm.getUrl(), projectForm.getProjectName(), principal);
            return new ModelAndView("redirect:/projects");
        }
        if(projectErrors.hasErrors()){
            return modelAndView.addObject("projectName", projectForm.getProjectName());
        }
        modelAndView.addObject("url", repositoryForm.getUrl())
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
