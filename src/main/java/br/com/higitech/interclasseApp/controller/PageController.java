package br.com.higitech.interclasseApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/setup/modalidades")
    public String setupModalidades() {
        return "forward:/setup/modalidades.html";
    }

    @GetMapping("/setup/torneios")
    public String setupTorneios() {
        return "forward:/setup/torneios.html";
    }

    @GetMapping("/setup/calendario")
    public String setupCalendario() {
        return "forward:/setup/calendario.html";
    }
}