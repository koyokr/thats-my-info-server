package com.rs.privacy.repository;

import com.rs.privacy.model.PrivacyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivacyInfoRepository extends JpaRepository<PrivacyInfo, Long> {

    PrivacyInfo findByCategory(String category);
}
