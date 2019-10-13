package net.kovand42.kova_design.web;

import net.kovand42.kova_design.entities.*;
import net.kovand42.kova_design.forms.ApplicationForm;
import net.kovand42.kova_design.forms.RepositoryForm;
import net.kovand42.kova_design.services.*;
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
    RepositoryService repositoryService;
    @GetMapping
    ModelAndView applications(){
        ModelAndView modelAndView = new ModelAndView("applications")
                .addObject("applications",applicationService.findAll());
        return modelAndView;
    }
    @GetMapping("{id}")
    ModelAndView application(@PathVariable long id){
        ModelAndView modelAndView = new ModelAndView("application");
        Application application = applicationService.findById(id).get();
        List<User> users = new LinkedList<>();
        application.getUserSkills()
                .stream().forEach(userSkill -> {
                    if(!users.contains(userSkill.getUser())){
                        users.add(userSkill.getUser());
                    }
        });
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
        modelAndView.addObject("skillsInUse", skillsInUse);
        modelAndView.addObject("user", user);
        modelAndView.addObject("skills", principalSkills);
        modelAndView.addObject("applicationForm", new ApplicationForm(null, null));
        return modelAndView;
    }
    @GetMapping("newApplicationSkills")
    ModelAndView newApplicationSkills(@RequestParam("id") long id, RedirectAttributes redirect){
        skillsForNewApp.add(id);
        return new ModelAndView("redirect:/applications/newApplication");
    }
    @GetMapping("remove")
    ModelAndView remove(@RequestParam("id") long id, RedirectAttributes redirect){
        skillsForNewApp.remove(id);
        return new ModelAndView("redirect:/applications/newApplication");
    }
    @GetMapping("create/repo")
    ModelAndView ceateRepo(@RequestParam("repoUrl") String url, RedirectAttributes redirect){
        if(repositoryService.findByUrl(url).isPresent()){
            redirect.addAttribute("repo", repositoryService.findByUrl(url).get().getRepositoryId());
            return new ModelAndView("redirect:/applications/create");
        }
        return new ModelAndView("newRepo", "repoForm", new RepositoryForm(null, null));
    }
    @PostMapping("create")
    ModelAndView create(@RequestParam("repo")Optional<Repository> repo,
                        RedirectAttributes redirect, Principal principal){
        User user = userService.findByUsername(principal.getName()).get();
        ModelAndView modelAndView = new ModelAndView("createApp").addObject("user", user);
        if(repo.isPresent()){
            modelAndView.addObject("appForm",new ApplicationForm(null, repo.get()));
            return modelAndView;
        }

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
    private void function(){
        /*        Application application =
                new Application(appForm.getApplicationName(), repo.get());
        skillsForNewApp.getNewSkills().stream().forEach(id -> {
            userSkillService.findBySkill(skillService.findById(id).get()).forEach(userSkill -> {
                if(userSkill.getUser().equals(user)){
                    application.add(userSkill);
                }
            });
        });
        applicationService.create(application);
        skillsForNewApp.getNewSkills().clear();*/
    }
}
