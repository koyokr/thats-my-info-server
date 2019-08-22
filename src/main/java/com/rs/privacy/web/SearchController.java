package com.rs.privacy.web;

import com.rs.privacy.model.PerosnTokenDTO;
import com.rs.privacy.model.SearchResultDTO;
import com.rs.privacy.model.TotalSearchResultDTO;
import com.rs.privacy.service.SearchService;
import com.rs.privacy.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<List<SearchResultDTO>> searchOld(PerosnTokenDTO perosnTokenDTO) {
        List<SearchResultDTO> searchResults = searchService.searchOld(perosnTokenDTO);

        return ResponseUtils.makeResponseEntity(searchResults, HttpStatus.OK);
    }

    @GetMapping("/search2")
    public ResponseEntity<TotalSearchResultDTO> search(PerosnTokenDTO perosnTokenDTO) {
        TotalSearchResultDTO result = searchService.search(perosnTokenDTO);

        return ResponseUtils.makeResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/existsNaverId")
    public ResponseEntity<Boolean> existsNaverId(String naverId) {
        Boolean exists = searchService.existsNaverId(naverId);

        return ResponseUtils.makeResponseEntity(exists, HttpStatus.OK);
    }
}
