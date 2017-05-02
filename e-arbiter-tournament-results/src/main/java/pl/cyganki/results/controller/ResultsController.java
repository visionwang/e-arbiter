package pl.cyganki.results.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ResultsController {

    @GetMapping("/test")
    public String test() {
        return "Results";
    }
}
