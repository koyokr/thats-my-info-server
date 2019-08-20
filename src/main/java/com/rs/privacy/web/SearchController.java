package com.rs.privacy.web;

import com.rs.privacy.model.SearchWithTokenDTO;
import com.rs.privacy.model.SearchResult;
import com.rs.privacy.service.SearchService;
import com.rs.privacy.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    private final SearchService personalInfoService;

    @Autowired
    public SearchController(SearchService personalInfoService) {
        this.personalInfoService = personalInfoService;
    }

    @PostMapping("/search")
    public ResponseEntity<List<SearchResult>> search(SearchWithTokenDTO clientInfoDTO) {
        List<SearchResult> searchResultList = personalInfoService.search(clientInfoDTO);

        return ResponseUtils.makeResponseEntity(searchResultList, HttpStatus.OK);
    }
}
