package pt.ipcb.carpooling.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/auth")
    public String auth(@RequestParam(name = "tab", defaultValue = "login") String tab, Model model) {
        model.addAttribute("tab", tab);
        return "auth";
    }
}
