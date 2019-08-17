package com.rs.privacy.web;

import com.rs.privacy.model.News;
import com.rs.privacy.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewsController {

    @Autowired
    private NewsRepository newsRepository;

    @GetMapping("/news")
    public Page<News> getNews(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }
}
