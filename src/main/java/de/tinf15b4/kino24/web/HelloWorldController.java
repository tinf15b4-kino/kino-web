package de.tinf15b4.kino24.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloWorldController {
    @RequestMapping("/greet")
    public String greet(Model model) {
        model.addAttribute("name", "Universe");
        return "greet";
    }
}
