package net.kovand42.kova_design.web;

import net.kovand42.kova_design.entities.*;
import net.kovand42.kova_design.forms.ProjectForm;
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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@RequestMapping("/projects")
public class ProjectController {
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
        List<Skill> localProjectSkills = makeProjectSkills(project);
        List<User> users = makeProjectUserList(project);
        User user = userService.findByUsername(principal.getName()).get();
        boolean cont = users.contains(user);
        modelAndView.addObject("cont", cont);
        modelAndView.addObject("projectSkills", localProjectSkills);
        modelAndView.addObject("lackingSkills", lackingProjectSkills(project));
        modelAndView.addObject("user", user);
        modelAndView.addObject("principal", principal);
        modelAndView.addObject("users", users);
        modelAndView.addObject("project", project);
        modelAndView.addObject("lackingUserSkills", lackingUserSkills(project));
        modelAndView.addObject("usersWithLackingProjectSkill", makeLackingSkillsUserList(project));
        return modelAndView;
    }
    @GetMapping("newProject")
    ModelAndView newProject(Principal principal){
        ModelAndView modelAndView = new ModelAndView("newProject");
        User user = userService.findByUsername(principal.getName()).get();
        List<Skill> principalSkills = makeSkillListFromUser(user);
        List<Skill> skillsInUse = new LinkedList<>();
        skillsForNewProject.getNewSkills().stream().forEach(id ->
                skillsInUse.add(skillService.findById(id).get()));
        modelAndView.addObject("empty", skillsInUse.isEmpty());
        modelAndView.addObject("lackingSkills", lackingSkills(user));
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
        addSkillToProject(projectService.findById(projectId).get(), id);
        StringBuilder strB = new StringBuilder();
        strB.append("redirect:/projects/").append(projectId);
        String redirectURL = strB.toString();
        return new ModelAndView(redirectURL);
    }
    @GetMapping("projectSkills/remove")
    ModelAndView removeSkill(@RequestParam("id") long id,
                             @RequestParam("projectId") long projectId,
                             RedirectAttributes redirect){
        removeSkillFromProject(projectService.findById(projectId).get(), id);
        StringBuilder strB = new StringBuilder();
        strB.append("redirect:/projects/").append(projectId);
        String redirectURL = strB.toString();
        return new ModelAndView(redirectURL);
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
        makeProjectFromUrlAndProjectName(repositoryForm.getUrl(), projectName, principal);
        return new ModelAndView("redirect:/projects");
    }
    @PostMapping("create")
    ModelAndView create(@Valid ProjectForm projectForm, Errors appErrors,
                        @Valid RepositoryForm repositoryForm, Errors repoErrors,
                        RedirectAttributes redirect, Principal principal){
        if(repositoryService.findByUrl(repositoryForm.getUrl()).isPresent()){
            makeProjectFromUrlAndProjectName(repositoryForm.getUrl(), projectForm.getProjectName(), principal);
            return new ModelAndView("redirect:/projects");
        }
        ModelAndView modelAndView = new ModelAndView("createProject")
                .addObject("url", repositoryForm.getUrl())
                .addObject("projectName", projectForm.getProjectName())
                .addObject("repoForm", new RepositoryForm(null, repositoryForm.getUrl()));
       return modelAndView;
    }
    private List<Skill> makeSkillListFromUser(User user){
        List<UserSkill> userSkills = userSkillService.findByUser(user);
        List<Skill> skills = new LinkedList<>();
        userSkills.forEach(userSkill -> {
            if(!skillsForNewProject.contains(userSkill.getSkill().getSkillId())){
                skills.add(userSkill.getSkill());
            }
        });
        return skills;
    }
    private void makeProjectFromUrlAndProjectName(String url, String projectName,
                                                  Principal principal){
        Repository repository = repositoryService.findByUrl(url).get();
        Project project = new Project(projectName, repository);
        User user = userService.findByUsername(principal.getName()).get();
        skillsForNewProject.getNewSkills().stream().forEach(id -> {
            userSkillService.findBySkill(skillService.findById(id).get()).forEach(userSkill -> {
                if(userSkill.getUser().equals(user)){
                    project.add(userSkill);
                }
            });
        });
        projectService.create(project);
        skillsForNewProject.setClear();
    }
    private List<Skill> lackingSkills(User user){
        List<Skill> userSkills = makeSkillListFromUser(user);
        List<Skill> masterSkills = makeMasterSkills();
        masterSkills.removeAll(userSkills);
        return masterSkills;
    }
    private List<Skill> lackingProjectSkills(Project project){
        List<Skill> masterSkills = makeMasterSkills();
        List<Skill> projectSkills = makeProjectSkills(project);
        masterSkills.removeAll(projectSkills);
        return masterSkills;
    }
    private List<Skill> lackingUserSkills(Project project){
        List<Skill> masterSkills = makeProjectSkills(project);
        List<User> users = makeProjectUserList(project);
        users.forEach(user -> {
            if(!(user.getUsername().equals("master"))){
                masterSkills.removeAll(makeSkillListFromUser(user));
            }
        });
        return masterSkills;
    }
    private List<Skill> makeMasterSkills(){
        User master = userService.findById(1).get();
        List<Skill> masterSkills = makeSkillListFromUser(master);
        return masterSkills;
    }
    private List<Skill> makeProjectSkills(Project project){
        List<Skill> projectSkillsLocale = new LinkedList<>();
        project.getUserSkills().stream().forEach(userSkill -> {
            if(!projectSkillsLocale.contains(userSkill.getSkill())){
                projectSkillsLocale.add(userSkill.getSkill());
            }
        });
        projectSkillsLocale.forEach(skill -> {
            projectSkills.add(skill.getSkillId());
        });
        return projectSkillsLocale;
    }
    private void removeSkillFromProject(Project project, long skillId){
        List<User> users = makeProjectUserList(project);
        projectSkills.remove(skillId);
        users.forEach(user -> {
            userSkillService.findByUser(user).forEach(userSkill -> {
                if (userSkill.getSkill().getSkillId() == skillId){
                    project.remove(userSkill);
                }
            });
        });
        projectService.update(project);
    }
    private void addSkillToProject(Project project, long id){
        List<User> users = makeProjectUserList(project);
        projectSkills.add(id);
        users.forEach(user -> {
            userSkillService.findByUser(user).forEach(userSkill -> {
                if(userSkill.getSkill().getSkillId() == id){
                    project.add(userSkill);
                }
            });
        });
        projectService.update(project);
    }
    private List<User> makeProjectUserList(Project project){
        List<User> users = new LinkedList<>();
        project.getUserSkills()
                .stream().forEach(userSkill -> {
            if(!users.contains(userSkill.getUser())){
                users.add(userSkill.getUser());
            }
        });
        return users;
    }
    private List<User> makeLackingSkillsUserList(Project project){
        List<User> users = userService.findAll();
        List<User> lackingUsers = new LinkedList<>();
        users.remove(userService.findByUsername("master").get());
        List<Skill> lackingSkills = lackingUserSkills(project);
        users.forEach(user -> {
            List<Skill> userSkills = makeSkillListFromUser(user);
            AtomicBoolean need = new AtomicBoolean(false);
            userSkills.forEach(userSkill -> {
                if(lackingSkills.contains(userSkill)){
                    need.set(true);
                }
            });
            if((need.get())&&!(makeProjectUserList(project).contains(user))){
                lackingUsers.add(user);
            }
        });
        return lackingUsers;
    }
}
