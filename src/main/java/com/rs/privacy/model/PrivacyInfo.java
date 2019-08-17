package com.rs.privacy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class PrivacyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, name = "category")
    private String category;

    @Column(nullable = false, name = "title")
    private String title;

    @Column(nullable = false, name = "content")
    @Lob
    private String content;

    public PrivacyInfo(PrivacyInfoDTO privacyInfoDTO) {
        this.category = privacyInfoDTO.getCategory();
        this.title = privacyInfoDTO.getTitle();
        this.content = privacyInfoDTO.getContent();
    }
}