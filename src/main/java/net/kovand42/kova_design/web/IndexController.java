package net.kovand42.kova_design.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/")
public class IndexController {
    private String hello(){
        int hour = LocalDateTime.now().getHour();
        if(hour >=6 && hour<12){
            return "goodMorning";
        }
        if(hour>=12 &&hour<18){
        return "goodAfternoon";
    }
        return "goodEvening";
    }


    @GetMapping
    ModelAndView index(){
        return new ModelAndView("index", "hello", hello());
    }

    @GetMapping("/about")
    ModelAndView about(){
        return new ModelAndView("about");
    }

    @GetMapping("/contact")
    ModelAndView contact(){
        return new ModelAndView("contact");
    }
}
