package com.rs.privacy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SearchResult {
    private String siteName;
    private String url;
    private List<String> contents;
    private Long numOfContents;

    public void setContents(List<String> contents) {
        this.contents = contents;
        setNumOfContents((long) contents.size());
    }
}
