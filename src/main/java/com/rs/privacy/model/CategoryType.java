package com.rs.privacy.model;

public enum CategoryType {
    PRIVACY_IS("개인정보란?"),
    PRIVACY_RULES("개인정보 보호 관련 법제"),
    PRIVACY_RULES_OF_USE("개인정보 보호 이용수칙"),
    PRIVACY_CAMPAIGN("내 정보 지킴이 켐페인");

    private String viewName;

    CategoryType(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }
}
