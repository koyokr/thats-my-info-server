package com.rs.privacy.web;

import com.rs.privacy.model.SiteAdminContactDTO;
import com.rs.privacy.model.SolveNaverDTO;
import com.rs.privacy.service.SolveService;
import com.rs.privacy.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/solve")
public class SolveController {

    @Autowired
    private SolveService solveService;

    @GetMapping("/naver")
    public ResponseEntity<Boolean> naver(SolveNaverDTO solveNaverDTO) {
        Boolean success = solveService.naver(solveNaverDTO);

        return ResponseUtils.makeResponseEntity(success, HttpStatus.OK);
    }

    @GetMapping("/contact")
    public ResponseEntity<SiteAdminContactDTO> contact(@RequestParam String url) {
        SiteAdminContactDTO contact = solveService.contact(url);

        return ResponseUtils.makeResponseEntity(contact, HttpStatus.OK);
    }
}
