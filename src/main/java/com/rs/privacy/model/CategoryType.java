package com.rs.privacy.model;

public enum CategoryType {
    PRIVACY_IS("개인정보란?"),
    PRIVACY_RULES("개인정보 법제");

    private String viewName;

    CategoryType(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }
}
