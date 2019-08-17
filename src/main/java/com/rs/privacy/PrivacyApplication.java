package com.rs.privacy;

import com.rs.privacy.service.CrawlService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class PrivacyApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(PrivacyApplication.class, args);

        CrawlService cs = new CrawlService();
        cs.CrawlService();

    }

}
