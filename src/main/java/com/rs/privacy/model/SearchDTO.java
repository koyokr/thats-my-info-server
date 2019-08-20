package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {
    private String naverId;
    private String phone;
    private String name;
    private String email;
    private String nickname;
}
