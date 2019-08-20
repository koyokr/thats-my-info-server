package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrivacyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "category")
    private String category;

    @Column(nullable = false, name = "title")
    private String title;

    @Lob
    @Column(nullable = false, name = "content")
    @org.hibernate.annotations.Type(type="org.hibernate.type.TextType")
    private String content;

    public PrivacyInfo(PrivacyInfoDTO privacyInfoDTO) {
        this.category = privacyInfoDTO.getCategory();
        this.title = privacyInfoDTO.getTitle();
        this.content = privacyInfoDTO.getContent();
    }
}