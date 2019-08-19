package com.rs.privacy.web;

import com.rs.privacy.model.AdminInfo;
import com.rs.privacy.model.PrivacyInfoDTO;
import com.rs.privacy.service.AdminService;
import com.rs.privacy.service.PrivacyInfoService;
import com.rs.privacy.utils.HttpSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
    Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private PrivacyInfoService privacyInfoService;

    @GetMapping
    public String main(HttpSession session) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/";
        }

       return "index";
    }

    @PostMapping("/login")
    public String login(HttpSession session, AdminInfo adminInfo) {
        log.debug("adminInfo: {}", adminInfo);


        if(adminService.login(session, adminInfo)) {
            return "redirect:/admin/manage";
        }

        return "redirect:/";
    }

    @GetMapping("/manage")
    public String manage(HttpSession session, Model model) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/";
        }

        model.addAttribute("privacyInfoList", privacyInfoService.findAll());

        return "/manage";
    }

    @GetMapping("/manage/form")
    public String form(HttpSession session) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/";
        }

        return "/form";
    }

    @PostMapping("/manage/form/upload")
    public String upload(HttpSession session, PrivacyInfoDTO privacyInfoDTO) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/";
        }

        privacyInfoService.create(privacyInfoDTO);

        return "redirect:/admin/manage";
    }

    @GetMapping("/manage/{id}")
    public String showPrivacyInfo(HttpSession session, @PathVariable Long id, Model model) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/";
        }

        model.addAttribute("privacyInfo", privacyInfoService.findById(id));

        return "/show";
    }

    @DeleteMapping("/manage/{id}/delete")
    public String deletePrivacyInfo(HttpSession session, @PathVariable Long id) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/";
        }

        privacyInfoService.delete(id);

        return "redirect:/admin/manage";
    }
}
