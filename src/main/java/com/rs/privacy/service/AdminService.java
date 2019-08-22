package com.rs.privacy.service;

import com.rs.privacy.model.AdminInfo;
import com.rs.privacy.repository.AdminInfoRepository;
import com.rs.privacy.utils.HttpSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class AdminService {

    @Autowired
    private AdminInfoRepository adminInfoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Boolean login(HttpSession session, AdminInfo adminInfo) {
        AdminInfo savedAdminInfo = adminInfoRepository.findById(adminInfo.getId())
                .orElseThrow(RuntimeException::new);

        if (passwordEncoder.matches(adminInfo.getPw(), savedAdminInfo.getPw())) {
            session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, savedAdminInfo);
            return true;
        }
        return false;
    }

    public Integer getFailCount(AdminInfo adminInfo) {
        AdminInfo savedAdminInfo = adminInfoRepository.findById(adminInfo.getId())
                .orElseThrow(RuntimeException::new);
        return savedAdminInfo.getFailCount();
    }

    public void IncrementFailCount(AdminInfo adminInfo) {
        AdminInfo savedAdminInfo = adminInfoRepository.findById(adminInfo.getId())
                .orElseThrow(RuntimeException::new);

        savedAdminInfo.setFailCount(savedAdminInfo.getFailCount() + 1);
        adminInfoRepository.save(savedAdminInfo);
    }

    public void resetFailCount(AdminInfo adminInfo) {
        AdminInfo savedAdminInfo = adminInfoRepository.findById(adminInfo.getId())
                .orElseThrow(RuntimeException::new);

        savedAdminInfo.setFailCount(0);
        adminInfoRepository.save(savedAdminInfo);
    }
}
