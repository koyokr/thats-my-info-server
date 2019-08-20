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
public class CrawlTodayHumor {
    private static String URL;
    private String siteName;
    private List<String> ContentsLists;
    private long numOfContents = 0;
    private String userID;
    private String clubID;

    public CrawlTodayHumor(String userID) {
        init(userID);
        crawl();
    }

    public void init(String userID) {
        this.userID = userID;
    }

    public void crawl() {
        URL = "http://www.todayhumor.co.kr/board/list.php?kind=search&keyfield=name&keyword=" +
                this.userID +
                "&Submit.x=0&Submit.y=0";
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements element = doc.select("tbody");
        Iterator<Element> date = element.select("td.date").iterator();
        Iterator<Element> content = element.select(".subject a").iterator();

        ContentsLists = new ArrayList<String>();

        while (content.hasNext()) {
            Element dateElement = date.next();
            Element contentElement = content.next();
            ContentsLists.add(dateElement.text() + " " + contentElement.text());
            numOfContents++;
        }

        //출력
        for (String Data : ContentsLists) {
            System.out.println(Data);
        }
    }
}
