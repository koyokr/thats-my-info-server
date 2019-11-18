package com.rs.privacy.web;

import com.rs.privacy.model.AdminInfo;
import com.rs.privacy.model.NewsDTO;
import com.rs.privacy.model.PrivacyInfoDTO;
import com.rs.privacy.service.AdminService;
import com.rs.privacy.service.NewsService;
import com.rs.privacy.service.PrivacyInfoService;
import com.rs.privacy.utils.HttpSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private PrivacyInfoService privacyInfoService;

    @Autowired
    private NewsService newsService;

    @GetMapping
    public String main(HttpSession session) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "index";
        }

        return "redirect:/admin/manage";
    }

    @PostMapping("/login")
    public String login(HttpSession session, AdminInfo adminInfo) {
        Integer maxFailCount = 5;

        if (adminService.getFailCount(adminInfo) >= maxFailCount) {
            return "redirect:/admin";
        }

        if (adminService.login(session, adminInfo)) {
            adminService.resetFailCount(adminInfo);
            return "redirect:/admin/manage";
        } else {
            adminService.incrementFailCount(adminInfo);
            return "redirect:/admin";
        }
    }

    @GetMapping("/manage")
    public String manage(HttpSession session, Model model) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/admin";
        }

        model.addAttribute("privacyInfoList", privacyInfoService.findAll());

        return "/manage";
    }

    @GetMapping("/manage/form")
    public String form(HttpSession session) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/admin";
        }

        return "/form";
    }

    @PostMapping("/manage/form/upload")
    public String upload(HttpSession session, PrivacyInfoDTO privacyInfoDTO) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/admin";
        }

        privacyInfoService.create(privacyInfoDTO);

        return "redirect:/admin/manage";
    }

    @GetMapping("/manage/{id}")
    public String showPrivacyInfo(HttpSession session, @PathVariable Long id, Model model) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/admin";
        }

        model.addAttribute("privacyInfo", privacyInfoService.findById(id));

        return "/show";
    }

    @GetMapping("/manage/{id}/form")
    public String showUpdateForm(HttpSession session, @PathVariable Long id, Model model) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/admin";
        }

        model.addAttribute("privacyInfo", privacyInfoService.findById(id));

        return "/updateForm";
    }

    @PutMapping("/manage/{id}/update")
    public String updatePrivacyInfo(HttpSession session, @PathVariable Long id, PrivacyInfoDTO privacyInfoDTO) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/admin";
        }

        privacyInfoService.update(id, privacyInfoDTO);

        return "redirect:/admin/manage/" + id;
    }

    @DeleteMapping("/manage/{id}/delete")
    public String deletePrivacyInfo(HttpSession session, @PathVariable Long id) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/admin";
        }

        privacyInfoService.delete(id);

        return "redirect:/admin/manage";
    }

    @GetMapping("/crawlNews")
    public String showCrawledNews(HttpSession session, Model model) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/admin";
        }

        model.addAttribute("newsList", newsService.findList());

        return "/news";
    }

    @GetMapping("/crawlNews/form")
    public String crawlNewsForm(HttpSession session) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/admin";
        }

        return "/newsForm";
    }

    @PostMapping("/crawlNews/form/upload")
    public String uploadNews(HttpSession session, NewsDTO newsDTO) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/admin";
        }

        newsService.create(newsDTO);

        return "redirect:/admin/crawlNews";
    }

    @GetMapping("/crawlNews/{id}")
    public String showNews(HttpSession session, @PathVariable Long id, Model model) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/admin";
        }

        model.addAttribute("crawlNews", newsService.findById(id));

        return "/show";
    }

    @GetMapping("/crawlNews/{id}/form")
    public String showUpdateCrawlForm(HttpSession session, @PathVariable Long id, Model model) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/admin";
        }

        model.addAttribute("crawlNews", newsService.findById(id));

        return "/updateNewsForm";
    }

    @PutMapping("/crawlNews/{id}/update")
    public String updateNews(HttpSession session, @PathVariable Long id, NewsDTO newsDTO) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/admin";
        }

        newsService.update(id, newsDTO);

        return "redirect:/admin/crawlNews/" + id;
    }

    @DeleteMapping("/crawlNews/{id}/delete")
    public String deleteNews(HttpSession session, @PathVariable Long id) {
        if (!HttpSessionUtils.isSessionedUser(session)) {
            return "redirect:/admin";
        }

        newsService.delete(id);

        return "redirect:/admin/crawlNews";
    }
}
