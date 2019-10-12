package net.kovand42.kova_design.web;

import net.kovand42.kova_design.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;
    @GetMapping
    ModelAndView users(){
        ModelAndView modelAndView = new ModelAndView("users")
                .addObject("users", userService.findAll());
        return modelAndView;
    }
}
