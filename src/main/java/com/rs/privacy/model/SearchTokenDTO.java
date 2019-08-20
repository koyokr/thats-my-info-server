package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchTokenDTO {
    private String naverId;
    private String phone;
    private String accessToken;
}
