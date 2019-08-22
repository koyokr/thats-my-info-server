package com.rs.privacy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rs.privacy.model.PerosnTokenDTO;
import com.rs.privacy.model.PersonDTO;
import com.rs.privacy.model.SearchResultDTO;
import com.rs.privacy.model.TotalSearchResultDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class SearchService {

    private final static String NAVER_CLIENT_ID = "iiTpSGlHgIkuycXWTh3N";
    private final static String NAVER_CLIENT_SECRET = "34ZpK2RcPz";
    private final static String KAKAO_REST_API_KEY = "d97e7d7df1703411d17522aa8c12e3f6";
    private final static String AZURE_BING_SEARCH_KEY = "01aefdbbc9ae4a9283582e2bb50d853d";

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    public List<SearchResultDTO> searchOld(PerosnTokenDTO perosnTokenDTO) {
        PersonDTO personDTO = getSearchDTO(perosnTokenDTO);
        if (personDTO == null) {
            return null;
        }
        String naverId = personDTO.getNaverId();

        List<SearchResultDTO> resultList = new ArrayList<>();
        resultList.add(crawlBing(naverId));
        resultList.add(crawlClien(naverId));
        resultList.add(crawlDaumCafe(naverId));
        resultList.add(crawlDaumPaper(naverId));
        resultList.add(crawlDaumSite(naverId));
        resultList.add(crawlDC(naverId));
        resultList.add(crawlIlbe(naverId));
        resultList.add(crawlNaverCafe(naverId, "22830216"));
        resultList.add(crawlNaverCafe(naverId, "10050146"));
        resultList.add(crawlNaverCafe(naverId, "10050813"));
        resultList.add(crawlNaverCafe(naverId, "11262350"));
        resultList.add(crawlNaverKin(naverId));
        resultList.add(crawlNaverSearch(naverId));
        resultList.add(crawlTodayHumor(naverId));
        resultList.add(crawlTwitter(naverId));

        return resultList;
    }

    public TotalSearchResultDTO search(PerosnTokenDTO perosnTokenDTO) {
        PersonDTO personDTO = getSearchDTO(perosnTokenDTO);
        if (personDTO == null) {
            return null;
        }
        String naverId = personDTO.getNaverId();
        if (!existsNaverId(naverId)) {
            return null;
        }

        List<SearchResultDTO> searchResults = new ArrayList<>();
        searchResults.add(crawlBing(naverId));
        searchResults.add(crawlClien(naverId));
        searchResults.add(crawlDaumCafe(naverId));
        searchResults.add(crawlDaumPaper(naverId));
        searchResults.add(crawlDaumSite(naverId));
        searchResults.add(crawlDC(naverId));
        searchResults.add(crawlIlbe(naverId));
        searchResults.add(crawlNaverCafe(naverId, "22830216"));
        searchResults.add(crawlNaverCafe(naverId, "10050146"));
        searchResults.add(crawlNaverCafe(naverId, "10050813"));
        searchResults.add(crawlNaverCafe(naverId, "11262350"));
        searchResults.add(crawlNaverKin(naverId));
        searchResults.add(crawlNaverSearch(naverId));
        searchResults.add(crawlTodayHumor(naverId));
        searchResults.add(crawlTwitter(naverId));

        return new TotalSearchResultDTO(personDTO, searchResults);
    }

    public Boolean existsNaverId(String naverId) {
        String url = "https://nid.naver.com/user2/joinAjax.nhn?m=checkId&id=" + naverId;

        RestTemplate restTemplate = restTemplateBuilder.build();
        String result = restTemplate.getForObject(url, String.class);

        return "NNNNN".equals(result);
    }

    private PersonDTO getSearchDTO(PerosnTokenDTO perosnTokenDTO) {
        String url = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + perosnTokenDTO.getAccessToken());

        JsonNode node = getNode(url, headers);
        if (node == null) {
            return null;
        }
        String resultCode = node.get("resultcode").textValue();
        if (!"00".equals(resultCode)) {
            return null;
        }
        JsonNode response = node.get("response");

        return new PersonDTO(
                perosnTokenDTO.getNaverId(),
                perosnTokenDTO.getPhone(),
                response.get("name").textValue(),
                response.get("email").textValue(),
                response.get("nickname").textValue()
        );
    }

    private JsonNode getNode(String url, HttpHeaders headers) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return null;
        }
        return response.getBody();
    }

    private Document getDocument(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            return null;
        }
    }

    private String removeTag(String html) {
        return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

    private SearchResultDTO crawlBing(String id) {
        String url = "https://www.bing.com/search?q=" + id;
        SearchResultDTO result = new SearchResultDTO("빙 검색", url);

        String urlApi = "https://api.cognitive.microsoft.com/bing/v7.0/search?q=" + id + "&mkt=ko-kr";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Ocp-Apim-Subscription-Key", AZURE_BING_SEARCH_KEY);

        JsonNode node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }
        JsonNode webPages = node.get("webPages");
        if (webPages == null) {
            return result;
        }
        for (JsonNode value : webPages.get("value")) {
            String name = value.get("name").textValue();
            String snippet = value.get("snippet").textValue();
            result.addContent(name + " " + snippet);
        }
        return result;
    }

    private SearchResultDTO crawlClien(String id) {
        String url = "https://www.clien.net/service/search?q=" + id;
        SearchResultDTO result = new SearchResultDTO("클리앙", url);

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
            result.addContent(dateElement.text() + " " + titleElement.text());
        }
        return result;
    }

    private SearchResultDTO crawlDaumCafe(String id) {
        String url = "http://search.daum.net/search?w=cafe&q=" + id;
        SearchResultDTO result = new SearchResultDTO("다음 카페", url);

        String urlApi = "https://dapi.kakao.com/v2/search/cafe?query=" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_REST_API_KEY);

        JsonNode node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }
        for (JsonNode document : node.get("documents")) {
            String cafeName = document.get("cafename").textValue();
            String contents = document.get("contents").textValue();
            result.addContent(removeTag(cafeName + " " + contents));
        }
        return result;
    }

    private SearchResultDTO crawlDaumPaper(String id) {
        String url = "https://search.daum.net/search?w=web&q=" + id;
        SearchResultDTO result = new SearchResultDTO("다음 웹문서 검색", url);

        String urlApi = "https://dapi.kakao.com/v2/search/web?query=" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_REST_API_KEY);

        JsonNode node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }
        for (JsonNode document : node.get("documents")) {
            String title = document.get("title").textValue();
            String contents = document.get("contents").textValue();
            result.addContent(removeTag(title + " " + contents));
        }
        return result;
    }

    private SearchResultDTO crawlDaumSite(String id) {
        String url = "http://search.daum.net/search?w=site&q=" + id;
        SearchResultDTO result = new SearchResultDTO("다음 사이트 검색", url);

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
            result.addContent(contentElement.text() + " " + fUrlElement.text());
        }
        return result;
    }

    private SearchResultDTO crawlDC(String id) {
        String url = "https://gallog.dcinside.com/" + id;
        SearchResultDTO result = new SearchResultDTO("디시인사이드", url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }
        Element element = doc.selectFirst("#container");
        if (element == null) {
            return result;
        }
        Iterator<Element> cb1 = element.select("div.cont.box1").iterator();
        Iterator<Element> cb2 = element.select("div.cont.box2").iterator();

        for (Element dateElement : element.select("div.cont.box3 .date")) {
            Element titleElement = cb1.next();
            Element contentElement = cb2.next();
            result.addContent(dateElement.text() + " " + titleElement.text() + " " + contentElement.text());
        }
        return result;
    }

    private SearchResultDTO crawlIlbe(String id) {
        String url = "http://www.ilbe.com/list/ilbe?searchType=nick_name&search=" + id;
        SearchResultDTO result = new SearchResultDTO("일베", url);

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
            result.addContent(dateElement.text() + " " + contentElement.text());
        }
        return result;
    }

    private SearchResultDTO crawlNaverCafe(String id, String clubId) {
        String url = "https://m.cafe.naver.com/ArticleSearchList.nhn?search.searchBy=3" +
                "&search.query=" + id +
                "&search.clubid=" + clubId;
        SearchResultDTO result = new SearchResultDTO("네이버 카페", url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }
        String cafeName = doc.getElementsByAttributeValue("name", "title").attr("content");
        result.setSiteName(result.getSiteName() + " (" + cafeName + ")");

        Element element = doc.selectFirst("#articleList");
        if (element == null) {
            return result;
        }

        Elements lis = element.select("ul.list_writer li");
        for (Element li : lis) {
            Element title = li.selectFirst("div.post_area");
            Element time = element.selectFirst("span.time");
            Element unuse = title.selectFirst("span.icon_txt");
            if (unuse != null) {
                unuse.remove();
            }
            result.addContent(time.text() + " " + title.text());
        }
        return result;
    }

    private SearchResultDTO crawlNaverKin(String id) {
        String url = "https://kin.naver.com/profile/" + id;
        SearchResultDTO result = new SearchResultDTO("네이버 지식인", url);

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
            result.addContent(dateElement.text() + " Q:" + contentElementQ.text() + " A:" + contentElementA.text());
        }
        return result;
    }

    private SearchResultDTO crawlNaverSearch(String id) {
        String url = "https://search.naver.com/search.naver?where=article&query=" + id;
        SearchResultDTO result = new SearchResultDTO("네이버 검색", url);

        String urlApi = "https://openapi.naver.com/v1/search/webkr.json?query=" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);

        JsonNode node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }
        for (JsonNode item : node.get("items")) {
            String title = item.get("title").textValue();
            String description = item.get("description").textValue();
            result.addContent(removeTag(title + " " + description));
        }
        return result;
    }

    private SearchResultDTO crawlTodayHumor(String id) {
        String url = "http://www.todayhumor.co.kr/board/list.php?kind=search&keyfield=name&keyword=" + id;
        SearchResultDTO result = new SearchResultDTO("오늘의유머", url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }
        Elements views = doc.select("tbody tr.view");
        for (Element view : views) {
            Element subject = view.selectFirst("td.subject a");
            Element date = view.selectFirst("td.date");
            result.addContent(subject.text() + " " + date.text());
        }
        return result;
    }

    private SearchResultDTO crawlTwitter(String id) {
        String url = "https://twitter.com/" + id;
        SearchResultDTO result = new SearchResultDTO("트위터", url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }
        Element element = doc.selectFirst("div.js-tweet-text-container");
        if (element == null) {
            return result;
        }
        for (Element content : element.select("div.js-tweet-text-container")) {
            result.addContent(content.text());
        }
        return result;
    }
}