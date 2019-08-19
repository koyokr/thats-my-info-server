package com.rs.privacy.web;

import com.rs.privacy.model.SolveDTO;
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

    @PostMapping("/in-naver")
    public ResponseEntity<Boolean> inNaver(SolveDTO solveDTO) {
        Boolean success = solveService.inNaver(solveDTO);

        return ResponseUtils.makeResponseEntity(success, HttpStatus.OK);
    }

    @GetMapping("/normal")
    public ResponseEntity<String> normal(@RequestParam String url) {
        String contact = solveService.getWhois(url);

        return ResponseUtils.makeResponseEntity(contact, HttpStatus.OK);
    }
}
