package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminInfo {
    private String id;
    private String pw;

    public boolean matchPassword(AdminInfo adminInfo) {
        return this.pw.equals(adminInfo.getPw());
    }
}
