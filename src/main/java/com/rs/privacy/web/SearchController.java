package com.rs.privacy.web;

import com.rs.privacy.model.SearchResult;
import com.rs.privacy.model.SearchTokenDTO;
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
    public ResponseEntity<List<SearchResult>> search(SearchTokenDTO searchTokenDTO) {
        List<SearchResult> searchResultList = searchService.search(searchTokenDTO);

        return ResponseUtils.makeResponseEntity(searchResultList, HttpStatus.OK);
    }
}
