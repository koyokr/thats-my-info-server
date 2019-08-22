package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TotalSearchResultDTO {
    private PersonDTO person;
    private List<SearchResultDTO> results;
}
