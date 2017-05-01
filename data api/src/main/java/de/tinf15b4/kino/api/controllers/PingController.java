package de.tinf15b4.kino.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
    @RequestMapping(value = "rest/ping")
    public String ping() {
        return "pong";
    }
}
