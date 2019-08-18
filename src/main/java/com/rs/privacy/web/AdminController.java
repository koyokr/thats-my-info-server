package com.rs.privacy.web;

import com.rs.privacy.model.AdminInfo;
import com.rs.privacy.service.AdminService;
import com.rs.privacy.utils.HttpSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/")
    public String main() {
       return "index";
    }

    @PostMapping("/login")
    public String login(HttpSession session, AdminInfo adminInfo) {

        if(adminService.login(session, adminInfo)) {
            return "manage";
        }

        return "redirect:/";
    }

    @GetMapping("/manage")
    public String manage(HttpSession session) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/";
        }

        return "/manage";
    }

    @GetMapping("/manage/form")
    public String form(HttpSession session) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/";
        }

        return "/manage";
    }
}
