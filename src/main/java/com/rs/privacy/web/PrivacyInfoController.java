package com.rs.privacy.web;

import com.rs.privacy.model.PrivacyInfo;
import com.rs.privacy.model.PrivacyInfoDTO;
import com.rs.privacy.service.PrivacyInfoService;
import com.rs.privacy.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/rules/{id}")
    public ResponseEntity<PrivacyInfo> showPrivacyRule(@PathVariable Long id) {
        PrivacyInfo privacyRule = privacyInfoService.findById(id);

        return ResponseUtils.makeResponseEntity(privacyRule, HttpStatus.FOUND);
    }

    @GetMapping("/rules-of-use")
    public ResponseEntity<List<PrivacyInfo>> showPrivacyRulesOfUse() {
        List<PrivacyInfo> privacyRulesOfUse = privacyInfoService.getPrivacyRulesOfUseList();

        return ResponseUtils.makeResponseEntity(privacyRulesOfUse, HttpStatus.FOUND);
    }

    @GetMapping("/rules-of-use/{id}")
    public ResponseEntity<PrivacyInfo> showPrivacyRuleOfUse(@PathVariable Long id) {
        PrivacyInfo privacyRuleOfUse = privacyInfoService.findById(id);

        return ResponseUtils.makeResponseEntity(privacyRuleOfUse, HttpStatus.FOUND);
    }

    @GetMapping("/campaign")
    public ResponseEntity<List<PrivacyInfo>> showPrivacyCampaigns() {
        List<PrivacyInfo> privacyCampaigns = privacyInfoService.getPrivacyRulesCampaignList();

        return ResponseUtils.makeResponseEntity(privacyCampaigns, HttpStatus.FOUND);
    }

    @GetMapping("/campaign/{id}")
    public ResponseEntity<PrivacyInfo> showPrivacyCampaign(@PathVariable Long id) {
        PrivacyInfo privacyCampaign = privacyInfoService.findById(id);

        return ResponseUtils.makeResponseEntity(privacyCampaign, HttpStatus.FOUND);
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> createPrivacyInfo(PrivacyInfoDTO privacyInfoDTO) {
        privacyInfoService.create(privacyInfoDTO);

        return ResponseUtils.makeResponseEntity(HttpStatus.OK);
    }
}
