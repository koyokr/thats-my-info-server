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
public class CrawlDaumPaper {
    private static String URL;
    private String siteName;
    private List<String> ContentsLists;
    private long numOfContents = 0;
    private String userID;
    private String clubID;

    public CrawlDaumPaper(String userID) {
        init(userID);
        crawl();
    }

    public void init(String userID) {
        this.userID = userID;
    }

    public void crawl() {
        URL = "http://search.daum.net/search?w=web&nil_search=btn&DA=NTB&enc=utf8&lpp=10&q=" + this.userID;
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements element = doc.select("div.cont_inner");
        Iterator<Element> title = element.select(".f_eb.desc").iterator();

        ContentsLists = new ArrayList<String>();

        while (title.hasNext()) {
            Element contentElement = title.next();
            ContentsLists.add(contentElement.text());
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
