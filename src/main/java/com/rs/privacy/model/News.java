package com.rs.privacy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, name = "title", length = 32)
    private String title;

    @Column(nullable = false, name = "content")
    @Lob
    private String content;

    @Column(nullable = false, name = "created_at")
    private Timestamp createdAt;

    public News(NewsDTO newsDTO) {
        this.title = newsDTO.getTitle();
        this.content = newsDTO.getContent();
    }
}