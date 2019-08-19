package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminInfo {

    @Id
    @Column(name = "id")
    private String id;

    @Column(nullable = false, name = "pw")
    private String pw;

    public boolean matchPassword(AdminInfo adminInfo) {
        return this.pw.equals(adminInfo.getPw());
    }
}
