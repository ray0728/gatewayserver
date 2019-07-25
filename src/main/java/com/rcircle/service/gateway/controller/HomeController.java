package com.rcircle.service.gateway.controller;

import com.rcircle.service.gateway.model.Account;
import com.rcircle.service.gateway.model.LogFile;
import com.rcircle.service.gateway.model.Message;
import com.rcircle.service.gateway.services.*;
import com.rcircle.service.gateway.utils.Base64;
import com.rcircle.service.gateway.utils.MvcToolkit;
import org.bouncycastle.math.raw.Mod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {
    @Resource
    AccountService accountService;
    @Resource
    ReferenceService referenceService;
    @Resource
    private ResourceService resourceService;
    @Resource
    private MessageService messageService;

    @GetMapping("notifications")
    public String showNotification(Principal principal, ModelMap mm) {
        MvcToolkit.autoLoadTopMenuData(resourceService, mm);
        MvcToolkit.autoLoadSideBarData(resourceService, mm);
        MvcToolkit.autoLoadNewsData(messageService, mm);
        mm.addAttribute("title", "Notifications");
        mm.addAttribute("messages", messageService.getMessageList(Message.TYPE_SMS));
        mm.addAttribute("news", messageService.getMessageList(Message.TYPE_NEWS));
        mm.addAttribute("system", messageService.getMessageList(Message.TYPE_SYS));
        return "notification";
    }

    @GetMapping("profile")
    public String changeProfile(Principal principal, ModelMap mm){
        MvcToolkit.autoLoadTopMenuData(resourceService, mm);
        MvcToolkit.autoLoadSideBarData(resourceService, mm);
        MvcToolkit.autoLoadNewsData(messageService, mm);
        mm.addAttribute("title", "Change your profile");
        Account opAccount = accountService.getAccountInfo(0, principal.getName());
        mm.addAttribute("account", opAccount);
        return "profile";
    }

    @GetMapping(value = {"", "home"})
    public String showHomePage(Principal principal, ModelMap mm) {
        MvcToolkit.autoLoadTopMenuData(resourceService, mm);
        MvcToolkit.autoLoadSideBarData(resourceService, mm);
        MvcToolkit.autoLoadNewsData(messageService, mm);
        List<LogFile> logs = new ArrayList<>();
        resourceService.getAllBlogs(0, 0, null, 0, 0, 5, logs);
        mm.addAttribute("title", "- Simple & Living -");
        mm.addAttribute("logs", logs);
        return "index";
    }

    @GetMapping("join")
    public String signup(ModelMap mm) {
        MvcToolkit.autoLoadTopMenuData(resourceService, mm);
        MvcToolkit.autoLoadSideBarData(resourceService, mm);
        MvcToolkit.autoLoadNewsData(messageService, mm);
        mm.addAttribute("title", "Create new account");
        mm.addAttribute("quot", referenceService.getRandomQuotation());
        return "sign_up";
    }

    @GetMapping("login")
    public String login(ModelMap mm, @RequestParam(name = "info", required = false, defaultValue = "") String msg) {
        MvcToolkit.autoLoadTopMenuData(resourceService, mm);
        MvcToolkit.autoLoadSideBarData(resourceService, mm);
        MvcToolkit.autoLoadNewsData(messageService, mm);
        mm.addAttribute("title", "Login");
        if (!msg.isEmpty()) {
            mm.addAttribute("msg", Base64.decode(msg));
        }
        return "login";
    }

    @GetMapping("logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login/";
    }

    @GetMapping("about")
    public String about(ModelMap mm) {
        MvcToolkit.autoLoadTopMenuData(resourceService, mm);
        MvcToolkit.autoLoadSideBarData(resourceService, mm);
        MvcToolkit.autoLoadNewsData(messageService, mm);
        mm.addAttribute("authors", accountService.getAllAccountBasicInfo());
        mm.addAttribute("title", "About us");

        return "about";
    }

    @GetMapping("contact")
    public String contact(ModelMap mm) {
        MvcToolkit.autoLoadTopMenuData(resourceService, mm);
        MvcToolkit.autoLoadSideBarData(resourceService, mm);
        MvcToolkit.autoLoadNewsData(messageService, mm);
        mm.addAttribute("title", "Contact us");
        return "contact";
    }

    @GetMapping("error/{num}")
    public String showError(ModelMap mm, @PathVariable("num") int num) {
        MvcToolkit.autoLoadTopMenuData(resourceService, mm);
        MvcToolkit.autoLoadSideBarData(resourceService, mm);
        MvcToolkit.autoLoadNewsData(messageService, mm);
        mm.addAttribute("errnum", num);
        mm.addAttribute("title", "Contact us");
        mm.addAttribute("errdesc", HttpStatus.resolve(num).getReasonPhrase());
        return "error";
    }
}
