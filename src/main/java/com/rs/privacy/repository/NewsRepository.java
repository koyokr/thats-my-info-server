package com.rs.privacy.repository;

import com.rs.privacy.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
}
