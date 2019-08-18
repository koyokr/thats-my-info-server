package com.rs.privacy.web;

import com.rs.privacy.model.ClientInfoDTO;
import com.rs.privacy.model.SearchResult;
import com.rs.privacy.service.PersonalInfoService;
import com.rs.privacy.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PersonalInfoController {

    private final PersonalInfoService personalInfoService;

    @Autowired
    public PersonalInfoController(PersonalInfoService personalInfoService) {
        this.personalInfoService = personalInfoService;
    }

    @PostMapping("/search")
    public ResponseEntity<List<SearchResult>> search(ClientInfoDTO clientInfoDTO) {
        List<SearchResult> searchResultList = personalInfoService.search(clientInfoDTO);

        return ResponseUtils.makeResponseEntity(searchResultList, HttpStatus.OK);
    }
}
