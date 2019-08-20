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
public class CrawlNaverCafe {
    private static String URL;
    private String siteName;
    private List<String> ContentsLists;
    private long numOfContents = 0;
    private String userID;
    private String clubID;
    private boolean isNull;

    public CrawlNaverCafe(String userID) {
        String[] clubID = {"22830216", "10050146", "10050813", "11262350"};

        for (int i = 0; i < clubID.length; i++) {
            init(userID, clubID[i]);
            System.out.println("=====Cafe " + clubID[i] + " ====");
            crawl();
        }
    }

    //보안프로젝트 : 22830216 중고나라 : 10050146 파우더룸 : 10050813 디젤매니아 : 11262350

    public void init(String userID, String clubid) {
        this.userID = userID;
        this.clubID = clubid;
    }

    public void crawl() {
        URL = "https://m.cafe.naver.com/ArticleSearchList.nhn?" +
                "search.query=" +
                this.userID +
                "&search.menuid=" +
                "&search.searchBy=3" +
                "&search.sortBy=date" +
                "&search.clubid=" +
                this.clubID +
                "&search.option=0" +
                "&search.defaultValue=1";

        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements element = doc.select("#articleList");
        Iterator<Element> date = element.select(".time").iterator();
        Iterator<Element> title = element.select("div.post_area").iterator();

        ContentsLists = new ArrayList<String>();

        while (date.hasNext()) {
            Element dateElement = date.next();
            Element titleElement = title.next();
            Element spanElement = titleElement.selectFirst("span.icon_txt");
            if (spanElement != null) {
                spanElement.remove();
            }

            ContentsLists.add(dateElement.text() + " " + titleElement.text());
            numOfContents++;
        }

        //출력
        for (String Data : ContentsLists) {
            System.out.println(Data);
        }
        isNull = true;
    }
}


