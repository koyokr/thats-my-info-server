package com.rs.privacy.service;

import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
public class CrawlIlbe {
    private static String URL;
    private String siteName;
    private List<String> ContentsLists;
    private long numOfContents = 0;
    private String userID;
    private String clubID;

    public CrawlIlbe(String userID) {
        init(userID);
        crawl();
    }

    public void init(String userID) {
        this.userID = userID;
    }

    public void crawl() {
        URL = "http://www.ilbe.com/list/ilbe?searchType=nick_name&search=" + this.userID;
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements element = doc.select(".board-body");
        Iterator<Element> date = element.select(".date").iterator();
        Iterator<Element> content = element.select(".subject").iterator();

        List<String> crawlDate = new ArrayList<String>();
        List<String> crawlContent = new ArrayList<String>();

        ContentsLists = new ArrayList<String>();

        while (date.hasNext()) {
            Element dateElement = date.next();
            Element contentElement = content.next();

            crawlDate.add(dateElement.text());
            crawlContent.add(contentElement.text());

            for (int i = 0; i < 2; i++) {
                crawlDate.remove(0);
                crawlContent.remove(0);
            }

            ContentsLists.add(dateElement.text() + " " + contentElement.text());
            numOfContents++;
        }

        //출력
        for (String Data : ContentsLists) {
            System.out.println(Data);
        }
    }

    public String getSiteName() {
        return siteName;
    }

    public String getUrl() {
        return URL;
    }

    public long getnumOfContents() {
        return numOfContents;
    }

    public List<String> getContents() {
        return ContentsLists;
    }
}
