package com.rs.privacy.service;

import com.rs.privacy.model.AdminInfo;
import com.rs.privacy.repository.AdminInfoRepository;
import com.rs.privacy.utils.HttpSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class AdminService {
    Logger log = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private AdminInfoRepository adminInfoRepository;

    public boolean login(HttpSession session, AdminInfo adminInfo) {
        AdminInfo savedAdminInfo = adminInfoRepository.findById(adminInfo.getId()).orElseThrow(RuntimeException::new);

        log.debug("adminInfo : {}", savedAdminInfo);

        if (savedAdminInfo.matchPassword(adminInfo)) {
            session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, adminInfo);

            return true;
        }

        return false;
    }

}
