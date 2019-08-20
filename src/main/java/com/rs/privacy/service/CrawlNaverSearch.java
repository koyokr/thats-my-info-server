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
public class CrawlNaverSearch {
    private static String URL;
    private String siteName;
    private List<String> ContentsLists;
    private long numOfContents = 0;
    private String userID;
    private String clubID;

    public CrawlNaverSearch(String userID) {
        init(userID);
        crawl();
    }

    public void init(String userID) {
        this.userID = userID;
    }

    public void crawl() {
        URL = "https://search.naver.com/search.naver?where=article&sm=tab_jum&query=" +
                this.userID +
                "&qvt=0";
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements element = doc.select("#elThumbnailResultArea");
        Iterator<Element> date = element.select(".txt_inline").iterator();
        Iterator<Element> title = element.select(".sh_cafe_title").iterator();
        Iterator<Element> content = element.select(".sh_cafe_passage").iterator();

        ContentsLists = new ArrayList<String>();

        while (date.hasNext()) {
            Element dateElement = date.next();
            Element titleElement = title.next();
            Element contentElement = content.next();

            ContentsLists.add(dateElement.text() + " " + titleElement.text() + " " + contentElement.text());
            numOfContents++;
        }

        //출력
        for (String Data : ContentsLists) {
            System.out.println(Data);
        }
    }
}
