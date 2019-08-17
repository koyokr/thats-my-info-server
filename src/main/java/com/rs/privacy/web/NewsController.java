package com.rs.privacy.web;

import com.rs.privacy.model.News;
import com.rs.privacy.model.NewsDTO;
import com.rs.privacy.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/news")
    public ResponseEntity<List<News>> getNewsList() {
        List<News> newsList = newsService.findList();

        return makeResponseEntity(newsList, HttpStatus.FOUND);
    }

    @GetMapping("/news/{id}")
    public ResponseEntity<News> getNews(@PathVariable Long id) {
        News news = newsService.findNews(id);

        return makeResponseEntity(news, HttpStatus.FOUND);
    }

    @PostMapping("/news/upload")
    public ResponseEntity<Void> createNews(NewsDTO newsDTO) {

        newsService.create(newsDTO);

        return makeResponseEntity(HttpStatus.OK);
    }

    private ResponseEntity<Void> makeResponseEntity(HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(headers, status);
    }

    private <T> ResponseEntity<T> makeResponseEntity(T object, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(object, headers, status);
    }
}
