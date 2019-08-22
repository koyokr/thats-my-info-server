package com.rs.privacy.model;

import lombok.AccessLevel;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "title")
    private String title;

    @Lob
    @Column(nullable = false, name = "content")
    @org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
    private String content;

    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false, name = "created_at")
    private Timestamp createdAt;

    public News(NewsDTO newsDTO) {
        title = newsDTO.getTitle();
        content = newsDTO.getContent();
    }

    public Timestamp getCreatedAt() {
        return new Timestamp(createdAt.getTime());
    }

    public void update(NewsDTO newsDTO) {
        title = newsDTO.getTitle();
        content = newsDTO.getContent();
    }
}
