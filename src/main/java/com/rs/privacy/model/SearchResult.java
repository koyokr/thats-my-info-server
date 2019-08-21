package com.rs.privacy.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SearchResult {
    private String siteName;
    private String url;
    private List<String> contents;
    private Long numOfContents;

    public SearchResult(String siteName, String url) {
        this.siteName = siteName;
        this.url = url;
        contents = new ArrayList();
        numOfContents = 0L;
    }

    public void setNumOfContents() {
        numOfContents = (long) contents.size();
    }
}
