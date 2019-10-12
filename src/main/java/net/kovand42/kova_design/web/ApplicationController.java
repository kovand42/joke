package net.kovand42.kova_design.web;

import net.kovand42.kova_design.entities.Application;
import net.kovand42.kova_design.entities.User;
import net.kovand42.kova_design.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/applications")
public class ApplicationController {
    @Autowired
    ApplicationService applicationService;
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
}
