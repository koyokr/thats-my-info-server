package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrivacyInfoDTO {
    private String title;
    private String category;
    private String content;
}
