package com.rs.privacy.web;

import com.rs.privacy.model.News;
import com.rs.privacy.model.NewsDTO;
import com.rs.privacy.service.NewsService;
import com.rs.privacy.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NewsController {

    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/news")
    public ResponseEntity<List<News>> getNewsList() {
        List<News> newsList = newsService.findList();

        return ResponseUtils.makeResponseEntity(newsList, HttpStatus.FOUND);
    }

    @GetMapping("/news/{id}")
    public ResponseEntity<News> getNews(@PathVariable Long id) {
        News news = newsService.findNews(id);

        return ResponseUtils.makeResponseEntity(news, HttpStatus.FOUND);
    }

    @PostMapping("/news/upload")
    public ResponseEntity<Void> createNews(NewsDTO newsDTO) {

        newsService.create(newsDTO);

        return ResponseUtils.makeResponseEntity(HttpStatus.OK);
    }


}
