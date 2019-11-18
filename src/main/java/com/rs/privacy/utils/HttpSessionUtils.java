package com.rs.privacy.utils;

import com.rs.privacy.model.AdminInfo;

import javax.servlet.http.HttpSession;

public class HttpSessionUtils {
    public static final String USER_SESSION_KEY = "sessionedUser";

    public static boolean isSessionedUser(HttpSession session) {
        return session.getAttribute(USER_SESSION_KEY) != null;
    }

    public static AdminInfo getSessionedUser(HttpSession session) {
        return (AdminInfo) session.getAttribute(USER_SESSION_KEY);
    }
}