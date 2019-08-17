package com.rs.privacy.web;

import com.rs.privacy.model.PrivacyInfo;
import com.rs.privacy.service.PrivacyInfoService;
import com.rs.privacy.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/info")
public class PrivacyInfoController {

    private final PrivacyInfoService privacyInfoService;

    @Autowired
    public PrivacyInfoController(PrivacyInfoService privacyInfoService) {
        this.privacyInfoService = privacyInfoService;
    }

    @GetMapping("/privacy-is")
    public ResponseEntity<PrivacyInfo> showPrivacyIs() {
        PrivacyInfo privacyIs = privacyInfoService.getPrivacyIs();

        return ResponseUtils.makeResponseEntity(privacyIs, HttpStatus.FOUND);
    }

    @GetMapping("/rules")
    public ResponseEntity<List<PrivacyInfo>> showPrivacyRules() {
        List<PrivacyInfo> privacyRules = privacyInfoService.getPrivacyRulesList();

        return ResponseUtils.makeResponseEntity(privacyRules, HttpStatus.FOUND);
    }
}
