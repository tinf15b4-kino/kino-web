package de.tinf15b4.kino.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RootRedirect {
    @RequestMapping("/")
    public String rootPath() {
        return "redirect:/greet";
    }
}
