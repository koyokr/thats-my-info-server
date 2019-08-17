package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class NewsDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
