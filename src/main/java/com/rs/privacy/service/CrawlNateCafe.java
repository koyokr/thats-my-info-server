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
public class CrawlNateCafe {
    private static String URL;
    private String siteName;
    private List<String> ContentsLists;
    private long numOfContents = 0;
    private String userID;
    private String clubID;

    public CrawlNateCafe(String userID) {
        init(userID);
        crawl();
    }

    public void init(String userID) {
        this.userID = userID;
    }

    public void crawl() {
        URL = "https://search.daum.net/nate?w=cafe&nil_search=btn&DA=NTB&enc=utf8&ASearchType=1&lpp=10&rlang=0&q=" + this.userID;
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements element = doc.select("div.coll_cont");
        Iterator<Element> cb1 = element.select(".wrap_tit.mg_tit").iterator();
        Iterator<Element> cb2 = element.select(".f_eb.desc").iterator();
        Iterator<Element> cb3 = element.select(".f_nb.date").iterator();

        ContentsLists = new ArrayList<String>();

        while (cb3.hasNext()) {
            Element dateElement = cb3.next();
            Element titleElement = cb1.next();
            Element contentElement = cb2.next();

            ContentsLists.add(dateElement.text() + " " + titleElement.text() + " " + contentElement.text());
            numOfContents++;
        }

        //출력
        for (String Data : ContentsLists) {
            System.out.println(Data);
        }
    }
}
