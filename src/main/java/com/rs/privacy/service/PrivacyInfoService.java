package com.rs.privacy.service;

import com.rs.privacy.model.CategoryType;
import com.rs.privacy.model.PrivacyInfo;
import com.rs.privacy.model.PrivacyInfoDTO;
import com.rs.privacy.repository.PrivacyInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrivacyInfoService {

    private final PrivacyInfoRepository privacyInfoRepository;

    @Autowired
    public PrivacyInfoService(PrivacyInfoRepository privacyInfoRepository) {
        this.privacyInfoRepository = privacyInfoRepository;
    }

    public PrivacyInfo getPrivacyIs() {
        return privacyInfoRepository.findByCategory(CategoryType.PRIVACY_IS.getViewName());
    }

    public List<PrivacyInfo> getPrivacyRulesList() {

       return privacyInfoRepository.findAll().stream()
                .filter(privacyInfo -> privacyInfo.getCategory().equals(CategoryType.PRIVACY_RULES.getViewName())).collect(Collectors.toList());
    }

    public void create(PrivacyInfoDTO privacyInfoDTO) {
        PrivacyInfo privacyInfo = new PrivacyInfo(privacyInfoDTO);

        privacyInfoRepository.save(privacyInfo);
    }

    public PrivacyInfo findById(Long id) {
        return privacyInfoRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public List<PrivacyInfo> getPrivacyRulesOfUseList() {

        return privacyInfoRepository.findAll().stream()
                .filter(privacyInfo -> privacyInfo.getCategory().equals(CategoryType.PRIVACY_RULES_OF_USE.getViewName())).collect(Collectors.toList());
    }

    public List<PrivacyInfo> getPrivacyRulesCampaignList() {

        return privacyInfoRepository.findAll().stream()
                .filter(privacyInfo -> privacyInfo.getCategory().equals(CategoryType.PRIVACY_CAMPAIGN.getViewName())).collect(Collectors.toList());
    }

    public List<PrivacyInfo> findAll() {
        return privacyInfoRepository.findAll();
    }

    public void delete(Long id) {
        privacyInfoRepository.deleteById(id);
    }

    public void update(Long id, PrivacyInfoDTO privacyInfoDTO) {
        PrivacyInfo savedPrivacyInfo = findById(id);
        savedPrivacyInfo.update(privacyInfoDTO);

        privacyInfoRepository.save(savedPrivacyInfo);
    }
}
