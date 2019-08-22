package com.rs.privacy.web;

import com.rs.privacy.model.SearchResult;
import com.rs.privacy.model.PerosnTokenDTO;
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
    public ResponseEntity<List<SearchResult>> search(PerosnTokenDTO perosnTokenDTO) {
        List<SearchResult> searchResultList = searchService.search(perosnTokenDTO);

        return ResponseUtils.makeResponseEntity(searchResultList, HttpStatus.OK);
    }
}
