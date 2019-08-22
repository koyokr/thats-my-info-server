package com.rs.privacy.service;

import com.rs.privacy.model.News;
import com.rs.privacy.model.NewsDTO;
import com.rs.privacy.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {

    private final NewsRepository newsRepository;

    @Autowired
    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<News> findList() {

        return newsRepository.findAll();
    }

    public News findNews(Long id) {

        return newsRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public void create(NewsDTO newsDTO) {
        News createdNews = new News(newsDTO);

        newsRepository.save(createdNews);
    }

    public News findById(Long id) {
        return newsRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public void update(Long id, NewsDTO newsDTO) {
        News savedNews = findById(id);
        savedNews.update(newsDTO);

        newsRepository.save(savedNews);
    }

    public void delete(Long id) {

        newsRepository.deleteById(id);
    }
}
