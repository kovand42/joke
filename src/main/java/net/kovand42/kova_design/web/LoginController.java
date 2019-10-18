package net.kovand42.kova_design.web;

import net.kovand42.kova_design.entities.Role;
import net.kovand42.kova_design.entities.User;
import net.kovand42.kova_design.forms.UserForm;
import net.kovand42.kova_design.services.RoleService;
import net.kovand42.kova_design.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("login")
@SessionAttributes("user")
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    private static final String VIEW = "login";
    private static final String REGISTRATION = "registration";

    @GetMapping
    ModelAndView login() {
        return new ModelAndView(VIEW);
    }
    @GetMapping("newUser")
    ModelAndView newUser(){
        ModelAndView modelAndView = new ModelAndView(REGISTRATION)
            .addObject(new UserForm(null, null, null, null));
        return modelAndView;
    }
    @PostMapping("newUser/registration")
    ModelAndView registration(@Valid UserForm userForm, Errors errors,
                              HttpServletRequest request, RedirectAttributes redirect){
        ModelAndView modelAndView = new ModelAndView("registration");
        if(errors.hasErrors()){
            return modelAndView;
        }
        if(userService.findByUsername(userForm.getUsername()).isPresent()){
            return modelAndView.addObject("message", "username is not free");
        }
        if(userService.findByEmail(userForm.getEmail()).isPresent()){
            return modelAndView.addObject("message", "email is not free");
        }
        User user = new User(userForm.getUsername(), userForm.getEmail(), new BCryptPasswordEncoder().encode(userForm.getPassword()), true);
        if(userForm.validatePaswoord(
                userForm.getPassword(), userForm.getConfirmPassword())){
            Role role = roleService.findByRoleName("user").get();
            role.add(user);
            userService.create(user);
            roleService.update(role);
            authWithHttpServletRequest(request, userForm.getUsername(), userForm.getPassword());
            return new ModelAndView("redirect:/projects");
        }
        return modelAndView.addObject("message", "password and confirm password don't match");
    }
    public void authWithHttpServletRequest(HttpServletRequest request, String username, String password) {
        try {
            request.login(username, password);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }
}
