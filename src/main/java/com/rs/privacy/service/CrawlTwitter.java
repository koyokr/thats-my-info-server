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
public class CrawlTwitter {
    private static String URL;
    private String siteName;
    private List<String> ContentsLists;
    private long numOfContents = 0;
    private String userID;
    private String clubID;

    public CrawlTwitter(String userID) {
        init(userID);
        crawl();
    }

    public void init(String userID) {
        this.userID = userID;
    }

    public void crawl() {
        URL = "https://twitter.com/" + this.userID;
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements element = doc.select("div.js-tweet-text-container");
        Iterator<Element> content = element.select("div.js-tweet-text-container").iterator();

        ContentsLists = new ArrayList<String>();

        while (content.hasNext()) {
            Element contentElement = content.next();

            ContentsLists.add(contentElement.text());
            numOfContents++;
        }

        //출력
        for (String Data : ContentsLists) {
            System.out.println(Data);
        }
    }
}
