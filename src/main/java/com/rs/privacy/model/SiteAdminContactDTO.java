package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SiteAdminContactDTO {
    private String name;
    private String email;
    private String phone;
    private String registrar;
}
