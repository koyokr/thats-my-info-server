package com.rs.privacy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rs.privacy.model.SearchDTO;
import com.rs.privacy.model.SearchResult;
import com.rs.privacy.model.SearchTokenDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class SearchService {

    private final static String NAVER_CLIENT_ID = "iiTpSGlHgIkuycXWTh3N";
    private final static String NAVER_CLIENT_SECRET = "34ZpK2RcPz";
    private final static String AZURE_BING_SEARCH_KEY = "01aefdbbc9ae4a9283582e2bb50d853d";

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    public List<SearchResult> search(SearchTokenDTO searchTokenDTO) {
        SearchDTO searchDTO = getSearchDTO(searchTokenDTO);
        String id = searchDTO.getNaverId();

        List<SearchResult> resultList = new ArrayList();
        resultList.add(crawlBing(id));
        resultList.add(crawlClien(id));
        resultList.add(crawlDaumCafe(id));
        resultList.add(crawlDaumPaper(id));
        resultList.add(crawlDaumSite(id));
        resultList.add(crawlDC(id));
        resultList.add(crawlIlbe(id));
        resultList.add(crawlNaverCafe(id, "22830216"));
        resultList.add(crawlNaverCafe(id, "10050146"));
        resultList.add(crawlNaverCafe(id, "10050813"));
        resultList.add(crawlNaverCafe(id, "11262350"));
        resultList.add(crawlNaverKin(id));
        resultList.add(crawlNaverSearch(id));
        resultList.add(crawlTodayHumor(id));
        resultList.add(crawlTwitter(id));

        return resultList;
    }

    private SearchDTO getSearchDTO(SearchTokenDTO searchTokenDTO) {
        String url = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + searchTokenDTO.getAccessToken());
        HttpEntity entity = new HttpEntity(headers);

        RestTemplate restTemplate = restTemplateBuilder.build();
        JsonNode node = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class).getBody();

        String resultCode = node.get("resultcode").textValue();
        if (!"00".equals(resultCode)) {
            return null;
        }
        JsonNode response = node.get("response");

        return new SearchDTO(
                searchTokenDTO.getNaverId(),
                searchTokenDTO.getPhone(),
                response.get("name").textValue(),
                response.get("email").textValue(),
                response.get("nickname").textValue()
        );
    }

    private SearchResult crawlBing(String id) {
        String urlView = "https://www.bing.com/search?q=" + id;
        SearchResult result = new SearchResult("빙 검색", urlView);

        String url = UriComponentsBuilder.fromHttpUrl("https://api.cognitive.microsoft.com/bing/v7.0/search")
                .queryParam("q", id)
                .queryParam("mkt", "ko-kr")
                .build().toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Ocp-Apim-Subscription-Key", AZURE_BING_SEARCH_KEY);
        HttpEntity entity = new HttpEntity(headers);

        RestTemplate restTemplate =  restTemplateBuilder.build();
        JsonNode node = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class).getBody();

        JsonNode pages = node.get("webPages");
        if (pages == null) {
            return result;
        }
        for (JsonNode value : pages.get("value")) {
            String name = value.get("name").textValue();
            String snippet = value.get("snippet").textValue();
            result.getContents().add(name + " " + snippet);
        }
        result.setNumOfContents();
        return result;
    }

    private SearchResult crawlClien(String id) {
        String url = "https://www.clien.net/service/search?q=" + id;
        SearchResult result = new SearchResult("클리앙", url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("div");
        Iterator<Element> date = element.select(".list_time").iterator();
        Iterator<Element> title = element.select(".list_title.oneline").iterator();

        while (date.hasNext()) {
            Element dateElement = date.next();
            Element titleElement = title.next();
            result.getContents().add(dateElement.text() + " " + titleElement.text());
        }
        result.setNumOfContents();

        return result;
    }

    private SearchResult crawlDaumCafe(String id) {
        String url = "http://search.daum.net/search?w=cafe&nil_search=btn&DA=NTB&enc=utf8&ASearchType=1&lpp=10&rlang=0&q=" + id;
        SearchResult result = new SearchResult("다음 카페", url);


        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("#cafeResultUL");
        if (element == null) {
            return result;
        }
        Iterator<Element> date = element.select(".f_nb.date").iterator();
        Iterator<Element> title = element.select(".wrap_tit.mg_tit").iterator();

        while (date.hasNext()) {
            Element dateElement = date.next();
            Element titleElement = title.next();
            result.getContents().add(dateElement.text() + " " + titleElement.text());
        }
        result.setNumOfContents();

        return result;
    }

    private SearchResult crawlDaumPaper(String id) {
        String url = "http://search.daum.net/search?w=web&nil_search=btn&DA=NTB&enc=utf8&lpp=10&q=" + id;
        SearchResult result = new SearchResult("다음 웹문서 검색", url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("#webdocColl");
        Iterator<Element> title = element.select(".f_eb.desc").iterator();

        while (title.hasNext()) {
            Element contentElement = title.next();
            result.getContents().add(contentElement.text());
        }
        result.setNumOfContents();

        return result;
    }

    private SearchResult crawlDaumSite(String id) {
        String url = "http://search.daum.net/search?w=site&nil_search=btn&DA=NTB&enc=utf8&lpp=10&q=" + id;
        SearchResult result = new SearchResult("다음 사이트 검색", url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("#siteColl");
        Iterator<Element> content = element.select("div.cont_inner").iterator();
        Iterator<Element> fUrl = element.select(".f_url").iterator();

        while (content.hasNext()) {
            Element contentElement = content.next();
            Element fUrlElement = fUrl.next();
            result.getContents().add(contentElement.text() + " " + fUrlElement.text());
        }
        result.setNumOfContents();

        return result;
    }

    private SearchResult crawlDC(String id) {
        String url = "https://gallog.dcinside.com/" + id;
        SearchResult result = new SearchResult("디시인사이드", url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("#container");
        Iterator<Element> cb1 = element.select("div.cont.box1").iterator();
        Iterator<Element> cb2 = element.select("div.cont.box2").iterator();
        Iterator<Element> cb3 = element.select("div.cont.box3 .date").iterator();

        while (cb3.hasNext()) {
            Element dateElement = cb3.next();
            Element titleElement = cb1.next();
            Element contentElement = cb2.next();
            result.getContents().add(dateElement.text() + " " + titleElement.text() + " " + contentElement.text());
        }
        result.setNumOfContents();

        return result;
    }

    private SearchResult crawlIlbe(String id) {
        String url = "http://www.ilbe.com/list/ilbe?searchType=nick_name&search=" + id;
        SearchResult result = new SearchResult("일베", url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst(".board-body");
        element.select(".title-line").remove();
        element.select(".notice-line").remove();
        element.select(".ad-line").remove();
        Iterator<Element> date = element.select(".date").iterator();
        Iterator<Element> content = element.select(".subject").iterator();

        while (date.hasNext()) {
            Element dateElement = date.next();
            Element contentElement = content.next();
            result.getContents().add(dateElement.text() + " " + contentElement.text());
        }
        result.setNumOfContents();

        return result;
    }

    private SearchResult crawlNaverCafe(String id, String clubId) {
        String url = "https://m.cafe.naver.com/ArticleSearchList.nhn?search.query=" + id +
                "&search.searchBy=3&search.sortBy=date&search.clubid=" + clubId +
                "&search.option=0&search.defaultValue=1";
        SearchResult result = new SearchResult("네이버 카페", url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("#articleList");
        if (element == null) {
            return result;
        }
        Iterator<Element> date = element.select(".time").iterator();
        Iterator<Element> title = element.select("div.post_area").iterator();

        while (date.hasNext()) {
            Element dateElement = date.next();
            Element titleElement = title.next();
            Element spanElement = titleElement.selectFirst("span.icon_txt");
            if (spanElement != null) {
                spanElement.remove();
            }
            result.getContents().add(dateElement.text() + " " + titleElement.text());
        }
        result.setNumOfContents();

        return result;
    }

    private SearchResult crawlNaverKin(String id) {
        String url = "https://kin.naver.com/profile/" + id;
        SearchResult result = new SearchResult("네이버 지식인", url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("tbody");
        if (element == null) {
            return result;
        }
        Iterator<Element> date = element.select("td.t_num.tc").iterator();
        Iterator<Element> contentQ = element.select("dt").iterator();
        Iterator<Element> contentA = element.select("dd").iterator();

        while (date.hasNext()) {
            Element dateElement = date.next();
            Element contentElementQ = contentQ.next();
            Element contentElementA = contentA.next();
            result.getContents().add(dateElement.text() + " Q:" + contentElementQ.text() + " A:" + contentElementA.text());
        }
        result.setNumOfContents();

        return result;
    }

    private SearchResult crawlNaverSearch(String id) {
        String urlView = "https://search.naver.com/search.naver?where=article&query=" + id;
        SearchResult result = new SearchResult("빙 검색", urlView);

        String url = UriComponentsBuilder.fromHttpUrl("https://openapi.naver.com/v1/search/webkr.json")
                .queryParam("query", id).build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        HttpEntity entity = new HttpEntity(headers);

        RestTemplate restTemplate =  restTemplateBuilder.build();
        JsonNode node = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class).getBody();

        for (JsonNode value : node.get("items")) {
            String title = value.get("title").textValue();
            String description = value.get("description").textValue();
            result.getContents().add(title + " " + description);
        }
        result.setNumOfContents();
        return result;
    }

    private SearchResult crawlTodayHumor(String id) {
        String url = "http://www.todayhumor.co.kr/board/list.php?kind=search&keyfield=name&keyword=" + id + "&Submit.x=0&Submit.y=0";
        SearchResult result = new SearchResult("오늘의유머", url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("tbody");
        Iterator<Element> date = element.select("td.date").iterator();
        Iterator<Element> content = element.select(".subject a").iterator();

        while (content.hasNext()) {
            Element dateElement = date.next();
            Element contentElement = content.next();
            result.getContents().add(dateElement.text() + " " + contentElement.text());
        }
        result.setNumOfContents();

        return result;
    }

    private SearchResult crawlTwitter(String id) {
        String url = "https://twitter.com/" + id;
        SearchResult result = new SearchResult("트위터", url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("div.js-tweet-text-container");
        Iterator<Element> content = element.select("div.js-tweet-text-container").iterator();

        while (content.hasNext()) {
            Element contentElement = content.next();
            result.getContents().add(contentElement.text());
        }
        result.setNumOfContents();

        return result;
    }

    private Document getDocument(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            return null;
        }
    }
}
