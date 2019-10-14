package net.kovand42.kova_design.web;

import net.kovand42.kova_design.entities.*;
import net.kovand42.kova_design.forms.ApplicationForm;
import net.kovand42.kova_design.forms.RepositoryForm;
import net.kovand42.kova_design.services.*;
import net.kovand42.kova_design.sessions.AppSkills;
import net.kovand42.kova_design.sessions.Open;
import net.kovand42.kova_design.sessions.SkillsForNewApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/applications")
public class ApplicationController {
    @Autowired
    ApplicationService applicationService;
    @Autowired
    UserSkillService userSkillService;
    @Autowired
    SkillService skillService;
    @Autowired
    UserService userService;
    @Autowired
    SkillsForNewApp skillsForNewApp;
    @Autowired
    AppSkills appSkills;
    @Autowired
    RepositoryService repositoryService;
    @GetMapping
    ModelAndView applications(Principal principal){
        appSkills.setClear();
        ModelAndView modelAndView = new ModelAndView("applications")
                .addObject("applications",applicationService.findAll())
                .addObject("principal", principal);
        return modelAndView;
    }
    @GetMapping("{id}")
    ModelAndView application(@PathVariable long id, Principal principal){
        ModelAndView modelAndView = new ModelAndView("application");
        Application application = applicationService.findById(id).get();
        List<Skill> localAppSkills = makeAppSkills(application);
        List<User> users = makeAppUserList(application);
        User user = userService.findByUsername(principal.getName()).get();
        boolean cont = users.contains(user);
        modelAndView.addObject("cont", cont);
        modelAndView.addObject("appSkills", localAppSkills);
        modelAndView.addObject("lackingSkills", lackingAppSkills(application));
        modelAndView.addObject("user", user);
        modelAndView.addObject("principal", principal);
        modelAndView.addObject("users", users);
        modelAndView.addObject("app", application);
        return modelAndView;
    }
    @GetMapping("newApplication")
    ModelAndView newApplication(Principal principal){
        ModelAndView modelAndView = new ModelAndView("newApplication");
        User user = userService.findByUsername(principal.getName()).get();
        List<Skill> principalSkills = makeSkillListFromUser(user);
        List<Skill> skillsInUse = new LinkedList<>();
        skillsForNewApp.getNewSkills().stream().forEach(id ->
                skillsInUse.add(skillService.findById(id).get()));
        modelAndView.addObject("lackingSkills", lackingSkills(user));
        modelAndView.addObject("skillsInUse", skillsInUse);
        modelAndView.addObject("user", user);
        modelAndView.addObject("skills", principalSkills);
        modelAndView.addObject("applicationForm", new ApplicationForm(null, null));
        modelAndView.addObject("repositoryForm", new RepositoryForm(null, null));
        return modelAndView;
    }
    @GetMapping("newApplicationSkills")
    ModelAndView newApplicationSkills(@RequestParam("id") long id, RedirectAttributes redirect){
        skillsForNewApp.add(id);
        return new ModelAndView("redirect:/applications/newApplication");
    }
    @GetMapping("ApplicationSkills")
    ModelAndView ApplicationSkills(@RequestParam("id") long id,
                                   @RequestParam("appId") long appId,
                                   RedirectAttributes redirect){
        addSkillToApp(applicationService.findById(appId).get(), id);
        StringBuilder strB = new StringBuilder();
        strB.append("redirect:/applications/").append(appId);
        String redirectURL = strB.toString();
        return new ModelAndView(redirectURL);
    }
    @GetMapping("ApplicationSkills/remove")
    ModelAndView removeSkill(@RequestParam("id") long id,
                             @RequestParam("appId") long appId,
                             RedirectAttributes redirect){
        removeSkillFromApp(applicationService.findById(appId).get(), id);
        StringBuilder strB = new StringBuilder();
        strB.append("redirect:/applications/").append(appId);
        String redirectURL = strB.toString();
        return new ModelAndView(redirectURL);
    }
    @GetMapping("newApplicationSkills/remove")
    ModelAndView remove(@RequestParam("id") long id, RedirectAttributes redirect){
        skillsForNewApp.remove(id);
        return new ModelAndView("redirect:/applications/newApplication");
    }
    @PostMapping("create/repo")
    ModelAndView ceateRepo(@Valid RepositoryForm repositoryForm,
            Errors errors, @RequestParam("appName") String appName,
                           @RequestParam("url") String url,
                           RedirectAttributes redirect, Principal principal){
        ModelAndView modelAndView = new ModelAndView("createApp");
        if(errors.hasErrors()){
            return modelAndView;
        }
        Repository repository = new Repository(repositoryForm.getRepositoryName(), url);
        repositoryService.create(repository);
        makeAppFromUrlAndAppName(repositoryForm.getUrl(), appName, principal);
        return new ModelAndView("redirect:/applications");
    }
    @PostMapping("create")
    ModelAndView create(@Valid ApplicationForm applicationForm, Errors appErrors,
                        @Valid RepositoryForm repositoryForm, Errors repoErrors,
                        RedirectAttributes redirect, Principal principal){
        System.out.println(repositoryForm.getUrl());
        System.out.println(applicationForm.getApplicationName());
        if(repositoryService.findByUrl(repositoryForm.getUrl()).isPresent()){
            makeAppFromUrlAndAppName(repositoryForm.getUrl(), applicationForm.getApplicationName(), principal);
            return new ModelAndView("redirect:/applications");
        }
        ModelAndView modelAndView = new ModelAndView("createApp")
                .addObject("url", repositoryForm.getUrl())
                .addObject("appName", applicationForm.getApplicationName())
                .addObject("repoForm", new RepositoryForm(null, repositoryForm.getUrl()));
       return modelAndView;
    }
    private List<Skill> makeSkillListFromUser(User user){
        List<UserSkill> userSkills = userSkillService.findByUser(user);
        List<Skill> skills = new LinkedList<>();
        userSkills.forEach(userSkill -> {
            if(!skillsForNewApp.contains(userSkill.getSkill().getSkillId())){
                skills.add(userSkill.getSkill());
            }
        });
        return skills;
    }
    private void makeAppFromUrlAndAppName(String url, String appName,
                                          Principal principal){
        Repository repository = repositoryService.findByUrl(url).get();
        Application application = new Application(appName, repository);
        User user = userService.findByUsername(principal.getName()).get();
        skillsForNewApp.getNewSkills().stream().forEach(id -> {
            userSkillService.findBySkill(skillService.findById(id).get()).forEach(userSkill -> {
                if(userSkill.getUser().equals(user)){
                    application.add(userSkill);
                }
            });
        });
        applicationService.create(application);
        skillsForNewApp.setClear();
    }
    private List<Skill> lackingSkills(User user){
        List<Skill> userSkills = makeSkillListFromUser(user);
        List<Skill> masterSkills = makeMasterSkills();
        masterSkills.removeAll(userSkills);
        return masterSkills;
    }
    private List<Skill> lackingAppSkills(Application application){
        List<Skill> masterSkills = makeMasterSkills();
        List<Skill> appSkills = makeAppSkills(application);
        masterSkills.removeAll(appSkills);
        return masterSkills;
    }
    private List<Skill> makeMasterSkills(){
        User master = userService.findById(1).get();
        List<Skill> masterSkills = makeSkillListFromUser(master);
        return masterSkills;
    }
    private List<Skill> makeAppSkills(Application application){
        List<Skill> appSkillsLocale = new LinkedList<>();
        application.getUserSkills().stream().forEach(userSkill -> {
            if(!appSkillsLocale.contains(userSkill.getSkill())){
                appSkillsLocale.add(userSkill.getSkill());
            }
        });
        appSkillsLocale.forEach(skill -> {
            appSkills.add(skill.getSkillId());
        });
        return appSkillsLocale;
    }
    private void removeSkillFromApp(Application application, long skillId){
        List<User> users = makeAppUserList(application);
        appSkills.remove(skillId);
        users.forEach(user -> {
            userSkillService.findByUser(user).forEach(userSkill -> {
                if (userSkill.getSkill().getSkillId() == skillId){
                    application.remove(userSkill);
                }
            });
        });
        applicationService.update(application);
    }
    private void addSkillToApp(Application application, long id){
        List<User> users = makeAppUserList(application);
        appSkills.add(id);
        users.forEach(user -> {
            userSkillService.findByUser(user).forEach(userSkill -> {
                if(userSkill.getSkill().getSkillId() == id){
                    application.add(userSkill);
                }
            });
        });
        applicationService.update(application);
    }
    private List<User> makeAppUserList(Application application){
        List<User> users = new LinkedList<>();
        application.getUserSkills()
                .stream().forEach(userSkill -> {
            if(!users.contains(userSkill.getUser())){
                users.add(userSkill.getUser());
            }
        });
        return users;
    }
}
