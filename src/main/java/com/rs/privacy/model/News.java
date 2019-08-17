package com.rs.privacy.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Getter @Setter
@Entity
public class News {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String description;
}