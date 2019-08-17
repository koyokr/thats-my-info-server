package com.rs.privacy.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.Iterator;

public class CrawlService {
    private static String naverid;
    private static String url;
    private Document doc = null;

    public void CrawlService(){
        naverid = "everpall";
        crawlKin();
        crawlJoonggonara();
    }


    public void crawlKin() {
        //답변글 갯수, 내용, 작성날짜
        url = "https://kin.naver.com/profile/" + naverid;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements element = doc.select("tr");
        Iterator<Element> ie1 = element.select("dt").iterator();
        Iterator<Element> ie2 = element.select("dd").iterator();
        Iterator<Element> ie3 = element.select("td.t_num.tc").iterator();


        //Iterator<Element> ie1 = element.select("a._sp_each_url._sp_each_title").iterator();
        //Iterator<Element> ie2 = (element.select("dd").get(1)).iterator();
        //Iterator<Element> ie2 = element.select("dd").get(1).text().iterator();

        while (ie1.hasNext()) {
            System.out.println("============================================================");
            System.out.println(
                    "[Date : " + ie3.next().text() + "]\t" +
                    "[Question : " + ie1.next().text() + "]\t" +
                    "[Answer : " + ie2.next().text() + "]\t"
            );
        }
    }

    public void crawlJoonggonara() {

        System.out.println("Joonggonara");
        //게시글 갯수, 내용
        url = "https://cafe.naver.com/joonggonara?" +
                "iframe_url=/ArticleSearchList.nhn%3" +
                "Fsearch.clubid=10050146%26" +
                "search.searchdate=all%26" +
                "search.searchBy=3%26" +
                "search.query="+naverid+"%26" +
                "search.defaultValue=1%26" +
                "search.sortBy=date";
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements element2 = doc.select("div");
        Iterator<Element> ie11 = element2.select("td.td_date").iterator();
        Iterator<Element> ie22 = element2.select("div.board-list").iterator();

        System.out.println(element2.select("div.board-list").text());
        System.out.println(element2.select("td.td_date").text());
        //Iterator<Element> ie1 = element.select("a._sp_each_url._sp_each_title").iterator();
        //Iterator<Element> ie2 = (element.select("dd").get(1)).iterator();
        //Iterator<Element> ie2 = element.select("dd").get(1).text().iterator();

        while (ie11.hasNext()) {
            System.out.println("============================================================");
            System.out.println(
                    "[Date : " + ie11.next().text() + "]\t" +
                            "[Title : " + ie22.next().text() + "]\t"
            );
        }
    }

        //댓글 갯수, 내용
}



