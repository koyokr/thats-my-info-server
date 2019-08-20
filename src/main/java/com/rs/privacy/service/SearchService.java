package com.rs.privacy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rs.privacy.model.SearchDTO;
import com.rs.privacy.model.SearchResult;
import com.rs.privacy.model.SearchTokenDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class SearchService {

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    public List<SearchResult> search(SearchTokenDTO searchTokenDTO) {
        SearchDTO searchDTO = getSearchDTO(searchTokenDTO);
        String id = searchDTO.getNaverId();

        List<SearchResult> resultList = new ArrayList<>();
        resultList.add(crawlBing(id));
        resultList.add(crawlClien(id));
        resultList.add(crawlDaumCafe(id));
        resultList.add(crawlDaumPaper(id));
        resultList.add(crawlDaumSite(id));
        resultList.add(crawlDC(id));
        resultList.add(crawlIlbe(id));
        resultList.add(crawlNateCafe(id));
        resultList.add(crawlNatePaper(id));
        resultList.add(crawlNateSite(id));
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
        if (!resultCode.equals("00")) {
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
        String url = "https://www.bing.com/search?q=" + id;

        SearchResult result = new SearchResult();
        result.setSiteName("빙 검색");
        result.setUrl(url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("div");
        Iterator<Element> date = element.select("li.b_algo h2").iterator();
        Iterator<Element> content = element.select("div.b_caption p").iterator();

        List<String> contents = new ArrayList<>();
        while (date.hasNext()) {
            Element dateElement = date.next();
            Element contentElement = content.next();
            contents.add(dateElement.text() + " " + contentElement.text());
        }

        result.setContents(contents);
        return result;
    }

    private SearchResult crawlClien(String id) {
        String url = "https://www.clien.net/service/search?q=" + id;

        SearchResult result = new SearchResult();
        result.setSiteName("클리앙");
        result.setUrl(url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("div");
        Iterator<Element> date = element.select(".list_time").iterator();
        Iterator<Element> title = element.select(".list_title.oneline").iterator();

        List<String> contents = new ArrayList<>();
        while (date.hasNext()) {
            Element dateElement = date.next();
            Element titleElement = title.next();
            contents.add(dateElement.text() + " " + titleElement.text());
        }

        result.setContents(contents);
        return result;
    }

    private SearchResult crawlDaumCafe(String id) {
        String url = "http://search.daum.net/search?w=cafe&nil_search=btn&DA=NTB&enc=utf8&ASearchType=1&lpp=10&rlang=0&q=" + id;

        SearchResult result = new SearchResult();
        result.setSiteName("다음 카페");
        result.setUrl(url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("div.cont_inner");
        Iterator<Element> date = element.select(".f_nb.date").iterator();
        Iterator<Element> title = element.select(".wrap_tit.mg_tit").iterator();

        List<String> contents = new ArrayList<>();
        while (date.hasNext()) {
            Element dateElement = date.next();
            Element titleElement = title.next();
            contents.add(dateElement.text() + " " + titleElement.text());
        }

        result.setContents(contents);
        return result;
    }

    private SearchResult crawlDaumPaper(String id) {
        String url = "http://search.daum.net/search?w=web&nil_search=btn&DA=NTB&enc=utf8&lpp=10&q=" + id;

        SearchResult result = new SearchResult();
        result.setSiteName("다음 웹문서 검색");
        result.setUrl(url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("div.cont_inner");
        Iterator<Element> title = element.select(".f_eb.desc").iterator();

        List<String> contents = new ArrayList<>();
        while (title.hasNext()) {
            Element contentElement = title.next();
            contents.add(contentElement.text());
        }

        result.setContents(contents);
        return result;
    }

    private SearchResult crawlDaumSite(String id) {
        String url = "http://search.daum.net/search?w=site&nil_search=btn&DA=NTB&enc=utf8&lpp=10&q=" + id;

        SearchResult result = new SearchResult();
        result.setSiteName("다음 웹사이트 검색");
        result.setUrl(url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("div.cont_inner");
        Iterator<Element> content = element.select("div.cont_inner").iterator();

        List<String> contents = new ArrayList<>();
        while (content.hasNext()) {
            Element contentElement = content.next();
            contents.add(contentElement.text());
        }

        result.setContents(contents);
        return result;
    }

    private SearchResult crawlDC(String id) {
        String url = "https://gallog.dcinside.com/" + id;

        SearchResult result = new SearchResult();
        result.setSiteName("디시인사이드");
        result.setUrl(url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("#container");
        Iterator<Element> cb1 = element.select("div.cont.box1").iterator();
        Iterator<Element> cb2 = element.select("div.cont.box2").iterator();
        Iterator<Element> cb3 = element.select("div.cont.box3 .date").iterator();

        List<String> contents = new ArrayList<>();
        while (cb3.hasNext()) {
            Element dateElement = cb3.next();
            Element titleElement = cb1.next();
            Element contentElement = cb2.next();
            contents.add(dateElement.text() + " " + titleElement.text() + " " + contentElement.text());
        }

        result.setContents(contents);
        return result;
    }

    private SearchResult crawlIlbe(String id) {
        String url = "http://www.ilbe.com/list/ilbe?searchType=nick_name&search=" + id;

        SearchResult result = new SearchResult();
        result.setSiteName("일베");
        result.setUrl(url);

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

        List<String> crawlDate = new ArrayList<>();
        List<String> crawlContent = new ArrayList<>();

        List<String> contents = new ArrayList<>();
        while (date.hasNext()) {
            Element dateElement = date.next();
            Element contentElement = content.next();

            crawlDate.add(dateElement.text());
            crawlContent.add(contentElement.text());

            for (int i = 0; i < 2; i++) {
                crawlDate.remove(0);
                crawlContent.remove(0);
            }
            contents.add(dateElement.text() + " " + contentElement.text());
        }

        result.setContents(contents);
        return result;
    }

    private SearchResult crawlNateCafe(String id) {
        String url = "https://search.daum.net/nate?w=cafe&nil_search=btn&DA=NTB&enc=utf8&ASearchType=1&lpp=10&rlang=0&q=" + id;

        SearchResult result = new SearchResult();
        result.setSiteName("네이트 카페");
        result.setUrl(url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("div.coll_cont");
        Iterator<Element> cb1 = element.select(".wrap_tit.mg_tit").iterator();
        Iterator<Element> cb2 = element.select(".f_eb.desc").iterator();
        Iterator<Element> cb3 = element.select(".f_nb.date").iterator();

        List<String> contents = new ArrayList<>();
        while (cb3.hasNext()) {
            Element dateElement = cb3.next();
            Element titleElement = cb1.next();
            Element contentElement = cb2.next();
            contents.add(dateElement.text() + " " + titleElement.text() + " " + contentElement.text());
        }

        result.setContents(contents);
        return result;
    }

    private SearchResult crawlNatePaper(String id) {
        String url = "https://search.daum.net/nate?w=web&nil_search=btn&DA=NTB&enc=utf8&lpp=10&q=" + id;

        SearchResult result = new SearchResult();
        result.setSiteName("네이트 웹문서 검색");
        result.setUrl(url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst(".list_info.clear");
        Iterator<Element> content = element.select(".f_eb.desc").iterator();

        List<String> contents = new ArrayList<>();
        while (content.hasNext()) {
            Element titleElement = content.next();
            contents.add(titleElement.text());
        }

        result.setContents(contents);
        return result;
    }

    private SearchResult crawlNateSite(String id) {
        String url = "https://search.daum.net/nate?w=site&nil_search=btn&DA=NTB&enc=utf8&lpp=10&q=" + id;

        SearchResult result = new SearchResult();
        result.setSiteName("네이트 웹사이트 검색");
        result.setUrl(url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst(".list_info.clear");
        Iterator<Element> date = element.select(".f_nb").iterator();
        Iterator<Element> fUrl = element.select(".f_url").iterator();

        List<String> contents = new ArrayList<>();
        while (date.hasNext()) {
            Element dateElement = date.next();
            Element urlElement = fUrl.next();
            contents.add(dateElement.text() + " " + urlElement.text());
        }

        result.setContents(contents);
        return result;
    }

    private SearchResult crawlNaverCafe(String id, String clubId) {
        // String[] clubID = {"22830216", "10050146", "10050813", "11262350"};
        String url = "https://m.cafe.naver.com/ArticleSearchList.nhn?" +
                "search.query=" + id +
                "&search.menuid=" +
                "&search.searchBy=3" +
                "&search.sortBy=date" +
                "&search.clubid=" + clubId +
                "&search.option=0" +
                "&search.defaultValue=1";

        SearchResult result = new SearchResult();
        result.setSiteName("네이버 카페");
        result.setUrl(url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("#articleList");
        Iterator<Element> date = element.select(".time").iterator();
        Iterator<Element> title = element.select("div.post_area").iterator();

        List<String> contents = new ArrayList<>();
        while (date.hasNext()) {
            Element dateElement = date.next();
            Element titleElement = title.next();
            Element spanElement = titleElement.selectFirst("span.icon_txt");
            if (spanElement != null) {
                spanElement.remove();
            }
            contents.add(dateElement.text() + " " + titleElement.text());
        }

        result.setContents(contents);
        return result;
    }

    private SearchResult crawlNaverKin(String id) {
        String url = "https://kin.naver.com/profile/" + id;

        SearchResult result = new SearchResult();
        result.setSiteName("네이버 지식인");
        result.setUrl(url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("tbody");
        Iterator<Element> date = element.select("td.t_num.tc").iterator();
        Iterator<Element> contentQ = element.select("dt").iterator();
        Iterator<Element> contentA = element.select("dd").iterator();

        List<String> contents = new ArrayList<>();
        while (date.hasNext()) {
            Element dateElement = date.next();
            Element contentElementQ = contentQ.next();
            Element contentElementA = contentA.next();
            contents.add(dateElement.text() + " Q:" + contentElementQ.text() + " A:" + contentElementA.text());
        }

        result.setContents(contents);
        return result;
    }

    private SearchResult crawlNaverSearch(String id) {
        String url = "https://search.naver.com/search.naver?where=article&sm=tab_jum&query=" + id + "&qvt=0";

        SearchResult result = new SearchResult();
        result.setSiteName("네이버 검색");
        result.setUrl(url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("#elThumbnailResultArea");
        Iterator<Element> date = element.select(".txt_inline").iterator();
        Iterator<Element> title = element.select(".sh_cafe_title").iterator();
        Iterator<Element> content = element.select(".sh_cafe_passage").iterator();

        List<String> contents = new ArrayList<>();
        while (date.hasNext()) {
            Element dateElement = date.next();
            Element titleElement = title.next();
            Element contentElement = content.next();
            contents.add(dateElement.text() + " " + titleElement.text() + " " + contentElement.text());
        }

        result.setContents(contents);
        return result;
    }

    private SearchResult crawlTodayHumor(String id) {
        String url = "http://www.todayhumor.co.kr/board/list.php?kind=search&keyfield=name&keyword=" + id + "&Submit.x=0&Submit.y=0";

        SearchResult result = new SearchResult();
        result.setSiteName("오늘의유머");
        result.setUrl(url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("tbody");
        Iterator<Element> date = element.select("td.date").iterator();
        Iterator<Element> content = element.select(".subject a").iterator();

        List<String> contents = new ArrayList<>();
        while (content.hasNext()) {
            Element dateElement = date.next();
            Element contentElement = content.next();
            contents.add(dateElement.text() + " " + contentElement.text());
        }

        result.setContents(contents);
        return result;
    }

    private SearchResult crawlTwitter(String id) {
        String url = "https://twitter.com/" + id;

        SearchResult result = new SearchResult();
        result.setSiteName("트위터");
        result.setUrl(url);

        Document doc = getDocument(url);
        if (doc == null) {
            return result;
        }

        Element element = doc.selectFirst("div.js-tweet-text-container");
        Iterator<Element> content = element.select("div.js-tweet-text-container").iterator();

        List<String> contents = new ArrayList<>();
        while (content.hasNext()) {
            Element contentElement = content.next();
            contents.add(contentElement.text());
        }

        result.setContents(contents);
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
