package com.rs.privacy.web;

import com.rs.privacy.model.SolveDTO;
import com.rs.privacy.service.SolveService;
import com.rs.privacy.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/solve")
public class SolveController {

    private final SolveService solveService;

    @Autowired
    public SolveController(SolveService solveService) {
        this.solveService = solveService;
    }

    @GetMapping("/in-naver")
    public ResponseEntity<Boolean> inNaver(SolveDTO solveDTO) {
        Boolean result = solveService.inNaver(solveDTO);

        return ResponseUtils.makeResponseEntity(result, HttpStatus.OK);
    }
}
