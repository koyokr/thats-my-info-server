package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PerosnTokenDTO {
    private String naverId;
    private String phone;
    private String accessToken;
}
