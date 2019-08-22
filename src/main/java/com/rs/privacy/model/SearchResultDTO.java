package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SearchResultDTO {
    private String siteName;
    private String url;
    private List<String> contents;
    private Integer numOfContents;

    public SearchResultDTO(String siteName, String url) {
        this.siteName = siteName;
        this.url = url;
        contents = new ArrayList<>();
        numOfContents = 0;
    }

    public void addContent(String content) {
        Integer maxLength = 40;
        if (content.length() > maxLength) {
            content = content.substring(0, maxLength);
        }
        contents.add(content);
        numOfContents = contents.size();
    }
}
