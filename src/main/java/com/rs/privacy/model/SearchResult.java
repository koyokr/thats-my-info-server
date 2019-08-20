package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SearchResult {
    private String siteName;
    private String url;
    private Long numOfContents;
    private List<String> contents;

    public SearchResult() {
        this.contents = new ArrayList<>();
    }

    public void setNumOfContents() {
        this.numOfContents = (long) this.contents.size();
    }
}
