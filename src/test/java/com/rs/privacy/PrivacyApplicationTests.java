package com.rs.privacy;

import com.rs.privacy.service.CrawlService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PrivacyApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void testCrawlService() {
        CrawlService cs = new CrawlService();
        cs.crawlKin();
        cs.crawlJoonggonara();
        System.out.println("11");
    }
}
