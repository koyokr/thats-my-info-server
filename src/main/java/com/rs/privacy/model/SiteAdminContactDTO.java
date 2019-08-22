package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SiteAdminContactDTO {
    private String name;
    private String email;
    private String phone;
    private String registrar;
}
