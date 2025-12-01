package dev.lilianagorga.mywebsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Liliana Gorga");
        return "index";
    }
}