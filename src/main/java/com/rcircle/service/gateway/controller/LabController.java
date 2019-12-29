package com.rcircle.service.gateway.controller;

import com.rcircle.service.gateway.services.LocationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping("/lab")
public class LabController {
    @Resource
    private LocationService locationService;

    @GetMapping("/voice")
    public String getMap(ModelMap mm) {
        mm.addAttribute("devices", locationService.getDevices());
        mm.addAttribute("title", "Voice of world");
        return "lab_voice_map";
    }

    @GetMapping("/voice/devices")
    public String getAllVoiceDevices(ModelMap mm){
        mm.addAttribute("devices", locationService.getDevices());
        return "lab_voice_map::devices-list";
    }
}
