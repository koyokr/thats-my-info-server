package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TotalSearchResultDTO {
    private PersonDTO personDTO;
    private List<SearchResultDTO> searchResults;
}
