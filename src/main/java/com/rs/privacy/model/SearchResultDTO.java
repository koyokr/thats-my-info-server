package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDTO {
    private String siteName;
    private String url;
    private List<ContentAndUrlDTO> contents;
    private Integer numOfContents;

    public SearchResultDTO(String siteName, String url) {
        this.siteName = siteName;
        this.url = url;
        contents = new ArrayList<>();
        numOfContents = 0;
    }

    public void addContent(String content, String url) {
        Integer maxLength = 40;
        if (content.length() > maxLength) {
            content = content.substring(0, maxLength);
        }
        contents.add(new ContentAndUrlDTO(content, url));
        numOfContents = contents.size();
    }
}
