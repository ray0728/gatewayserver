package com.rcircle.service.gateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lab")
public class LabController {

    @GetMapping("/voice")
    public String getMap(ModelMap mm){
        mm.addAttribute("title", "Voice of world");
        return "lab_voice_map";
    }

}
