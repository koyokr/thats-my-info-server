package com.rs.privacy.web;

import com.rs.privacy.model.SiteAdminContactDTO;
import com.rs.privacy.model.SolveNaverDTO;
import com.rs.privacy.service.SolveService;
import com.rs.privacy.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solve")
public class SolveController {

    private final SolveService solveService;

    @Autowired
    public SolveController(SolveService solveService) {
        this.solveService = solveService;
    }

    @GetMapping("/in-naver")
    public ResponseEntity<Boolean> inNaver(SolveNaverDTO solveNaverDTO) {
        Boolean success = solveService.inNaver(solveNaverDTO);

        return ResponseUtils.makeResponseEntity(success, HttpStatus.OK);
    }

    @GetMapping("/normal")
    public ResponseEntity<SiteAdminContactDTO> normal(@RequestParam String url) {
        SiteAdminContactDTO contact = solveService.getWhois(url);

        return ResponseUtils.makeResponseEntity(contact, HttpStatus.OK);
    }
}
