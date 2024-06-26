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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

        String id = personDTO.getNaverId();
        String name = personDTO.getName();
        String nickname = personDTO.getNickname();
        String phone = personDTO.getPhone();
        String email = personDTO.getEmail();

        List<SearchResultDTO> searchResults = new ArrayList<>();
        searchResults.add(crawlBing(id, name, nickname, phone, email));
        searchResults.add(crawlClien(id));
        searchResults.add(crawlDaumCafe(id, name, nickname, phone, email));
        searchResults.add(crawlDaumPaper(id, name, nickname, phone, email));
        searchResults.add(crawlDaumSite(id));
        searchResults.add(crawlDC(id));
        searchResults.add(crawlIlbe(id));
        searchResults.add(crawlNaverCafe(id, "22830216"));
        searchResults.add(crawlNaverCafe(id, "10050146"));
        searchResults.add(crawlNaverCafe(id, "10050813"));
        searchResults.add(crawlNaverCafe(id, "11262350"));
        searchResults.add(crawlNaverKin(id));
        searchResults.add(crawlNaverSearch(id, name, nickname, phone, email));
        searchResults.add(crawlTodayHumor(id));
        searchResults.add(crawlTwitter(id));
        searchResults.add(crawlGoogleSearch(id, name, nickname, phone, email));

        return searchResults;
    }

    public TotalSearchResultDTO search(PerosnTokenDTO perosnTokenDTO) {
        PersonDTO personDTO = getSearchDTO(perosnTokenDTO);
        if (personDTO == null) {
            return null;
        }
        String id = personDTO.getNaverId();
        String name = personDTO.getName();
        String nickname = personDTO.getNickname();
        String phone = personDTO.getPhone();
        String email = personDTO.getEmail();

        if (!existsNaverId(id)) {
            return null;
        }

        List<SearchResultDTO> searchResults = new ArrayList<>();
        searchResults.add(crawlBing(id, name, nickname, phone, email));
        searchResults.add(crawlClien(id));
        searchResults.add(crawlDaumCafe(id, name, nickname, phone, email));
        searchResults.add(crawlDaumPaper(id, name, nickname, phone, email));
        searchResults.add(crawlDaumSite(id));
        searchResults.add(crawlDC(id));
        searchResults.add(crawlIlbe(id));
        searchResults.add(crawlNaverCafe(id, "22830216"));
        searchResults.add(crawlNaverCafe(id, "10050146"));
        searchResults.add(crawlNaverCafe(id, "10050813"));
        searchResults.add(crawlNaverCafe(id, "11262350"));
        searchResults.add(crawlNaverKin(id));
        searchResults.add(crawlNaverSearch(id, name, nickname, phone, email));
        searchResults.add(crawlTodayHumor(id));
        searchResults.add(crawlTwitter(id));
        searchResults.add(crawlGoogleSearch(id, name, nickname, phone, email));

        return new TotalSearchResultDTO(personDTO, searchResults);
    }

    public Boolean existsNaverId(String naverId) {
        String url = "https://nid.naver.com/user2/joinAjax.nhn?m=checkId&id=" + naverId;

        RestTemplate restTemplate = restTemplateBuilder.build();
        String result = restTemplate.getForObject(url, String.class);

        return "NNNNN".equals(result);
    }

    private PersonDTO getSearchDTO(PerosnTokenDTO perosnTokenDTO) {
        String accessToken = perosnTokenDTO.getAccessToken();

        String url = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        JsonNode node = getNode(url, headers);
        if (node == null) {
            return null;
        }

        String urlDeleteToken = UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/token")
                .queryParam("grant_type", "delete")
                .queryParam("client_id", NAVER_CLIENT_ID)
                .queryParam("client_secret", NAVER_CLIENT_SECRET)
                .queryParam("access_token", accessToken)
                .queryParam("service_provider", "NAVER")
                .build().toUriString();
        getNode(urlDeleteToken);

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

    private JsonNode getNode(String url) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        return restTemplate.getForObject(url, JsonNode.class);
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

    private SearchResultDTO crawlBing(String id, String name, String nickname, String phone, String email) {
        String url = "https://www.bing.com/search?q=" + id;
        SearchResultDTO result = new SearchResultDTO("빙 검색", url);

        List<String> apiUrls = Arrays.asList(
                "https://api.cognitive.microsoft.com/bing/v7.0/search?mkt=ko-kr&q=" + id,
                "https://api.cognitive.microsoft.com/bing/v7.0/search?mkt=ko-kr&q=" + nickname,
                "https://api.cognitive.microsoft.com/bing/v7.0/search?mkt=ko-kr&q=" + "\"" + phone + "\"",
                "https://api.cognitive.microsoft.com/bing/v7.0/search?mkt=ko-kr&q=" + "\"" + email + "\"",
                "https://api.cognitive.microsoft.com/bing/v7.0/search?mkt=ko-kr&q=" + name + " " + "\"" + phone + "\"",
                "https://api.cognitive.microsoft.com/bing/v7.0/search?mkt=ko-kr&q=" + name + " " + "\"" + email + "\"",
                "https://api.cognitive.microsoft.com/bing/v7.0/search?mkt=ko-kr&q=" + name + " " + "\"" + id + "\"",
                "https://api.cognitive.microsoft.com/bing/v7.0/search?mkt=ko-kr&q=" + nickname + " " + "\"" + phone + "\"",
                "https://api.cognitive.microsoft.com/bing/v7.0/search?mkt=ko-kr&q=" + nickname + " " + "\"" + email + "\"",
                "https://api.cognitive.microsoft.com/bing/v7.0/search?mkt=ko-kr&q=" + nickname + " " + "\"" + id + "\"",
                "https://api.cognitive.microsoft.com/bing/v7.0/search?mkt=ko-kr&q=" + "\"" + phone + "\"" + " " + "\"" + id + "\"",
                "https://api.cognitive.microsoft.com/bing/v7.0/search?mkt=ko-kr&q=" + "\"" + email + "\"" + " " + "\"" + id + "\""
        );
        List<String> overlapCheck = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Ocp-Apim-Subscription-Key", AZURE_BING_SEARCH_KEY);

        for (String apiUrl : apiUrls) {
            JsonNode node = getNode(apiUrl, headers);
            if (node == null) {
                return result;
            }
            JsonNode webPages = node.get("webPages");
            if (webPages == null) {
                return result;
            }
            for (JsonNode value : webPages.get("value")) {
                String link = value.get("url").textValue();

                if (!overlapCheck.contains(link)) {
                    String nameText = value.get("name").textValue();
                    String snippet = value.get("snippet").textValue();
                    result.addContent(nameText + " " + snippet, link);
                }
                overlapCheck.add(link);
            }
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
            String url_temp = url;
            result.addContent(dateElement.text() + " " + titleElement.text(), url_temp);
        }
        return result;
    }

    private SearchResultDTO crawlDaumCafe(String id, String name, String nickname, String phone, String email) {
        String url = "http://search.daum.net/search?w=cafe&q=" + id;
        SearchResultDTO result = new SearchResultDTO("다음 카페", url);

        List<String> apiUrls = Arrays.asList(
                "https://dapi.kakao.com/v2/search/cafe?query=" + id,
                "https://dapi.kakao.com/v2/search/cafe?query=" + nickname,
                "https://dapi.kakao.com/v2/search/cafe?query=" + "\"" + phone + "\"",
                "https://dapi.kakao.com/v2/search/cafe?query=" + "\"" + email + "\"",
                "https://dapi.kakao.com/v2/search/cafe?query=" + name + " " + "\"" + phone + "\"",
                "https://dapi.kakao.com/v2/search/cafe?query=" + name + " " + "\"" + email + "\"",
                "https://dapi.kakao.com/v2/search/cafe?query=" + name + " " + "\"" + id + "\"",
                "https://dapi.kakao.com/v2/search/cafe?query=" + nickname + " " + "\"" + phone + "\"",
                "https://dapi.kakao.com/v2/search/cafe?query=" + nickname + " " + "\"" + email + "\"",
                "https://dapi.kakao.com/v2/search/cafe?query=" + nickname + " " + "\"" + id + "\"",
                "https://dapi.kakao.com/v2/search/cafe?query=" + "\"" + phone + "\"" + " " + "\"" + id + "\"",
                "https://dapi.kakao.com/v2/search/cafe?query=" + "\"" + email + "\"" + " " + "\"" + id + "\""
        );
        List<String> overlapCheck = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_REST_API_KEY);

        for (String apiUrl : apiUrls) {
            JsonNode node = getNode(apiUrl, headers);
            if (node == null) {
                return result;
            }
            JsonNode total_num_json = node.get("meta");
            int totalPaper = total_num_json.get("total_count").intValue();

            if (totalPaper < 100) {
                for (JsonNode document : node.get("documents")) {
                    String link = document.get("url").textValue();

                    if (!overlapCheck.contains(link)) {
                        String title = document.get("title").textValue();
                        String contents = document.get("contents").textValue();
                        result.addContent(removeTag(title + " " + contents), link);
                    }
                    overlapCheck.add(link);
                }
            }
        }
        return result;
    }

    private SearchResultDTO crawlDaumPaper(String id, String name, String nickname, String phone, String email) {
        String url = "https://search.daum.net/search?w=web&q=" + id;
        SearchResultDTO result = new SearchResultDTO("다음 웹문서 검색", url);

        List<String> apiUrls = Arrays.asList(
                "https://dapi.kakao.com/v2/search/web?query=" + id,
                "https://dapi.kakao.com/v2/search/web?query=" + nickname,
                "https://dapi.kakao.com/v2/search/web?query=" + "\"" + phone + "\"",
                "https://dapi.kakao.com/v2/search/web?query=" + "\"" + email + "\"",
                "https://dapi.kakao.com/v2/search/web?query=" + name + " " + "\"" + phone + "\"",
                "https://dapi.kakao.com/v2/search/web?query=" + name + " " + "\"" + email + "\"",
                "https://dapi.kakao.com/v2/search/web?query=" + name + " " + "\"" + id + "\"",
                "https://dapi.kakao.com/v2/search/web?query=" + nickname + " " + "\"" + phone + "\"",
                "https://dapi.kakao.com/v2/search/web?query=" + nickname + " " + "\"" + email + "\"",
                "https://dapi.kakao.com/v2/search/web?query=" + nickname + " " + "\"" + id + "\"",
                "https://dapi.kakao.com/v2/search/web?query=" + "\"" + phone + "\"" + " " + "\"" + id + "\"",
                "https://dapi.kakao.com/v2/search/web?query=" + "\"" + email + "\"" + " " + "\"" + id + "\""
        );
        List<String> overlapCheck = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_REST_API_KEY);

        for (String apiUrl : apiUrls) {
            JsonNode node = getNode(apiUrl, headers);
            if (node == null) {
                return result;
            }

            JsonNode totalNumJson = node.get("meta");
            int totalPaper = totalNumJson.get("total_count").intValue();

            if (totalPaper < 100) {
                for (JsonNode document : node.get("documents")) {
                    String link = document.get("url").textValue();

                    if (!overlapCheck.contains(link)) {
                        String title = document.get("title").textValue();
                        String contents = document.get("contents").textValue();
                        result.addContent(removeTag(title + " " + contents), link);
                    }
                    overlapCheck.add(link);
                }
            }
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
            String url_temp = "";
            result.addContent(contentElement.text() + " " + fUrlElement.text(), url_temp);
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
            String url_temp = "";
            result.addContent(dateElement.text() + " " + titleElement.text() + " " + contentElement.text(), url_temp);
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
            String url_temp = "";
            result.addContent(dateElement.text() + " " + contentElement.text(), url_temp);
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
            String url_temp = "";
            result.addContent(time.text() + " " + title.text(), url_temp);
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
            String url_temp = "";
            result.addContent(dateElement.text() + " Q:" + contentElementQ.text() + " A:" + contentElementA.text(), url_temp);
        }
        return result;
    }

    private SearchResultDTO crawlNaverSearch(String id, String name, String nickname, String phone, String email) {
        String url = "https://search.naver.com/search.naver?where=article&query=" + id;
        SearchResultDTO result = new SearchResultDTO("네이버 검색", url);

        List<String> apiUrls = Arrays.asList(
                "https://openapi.naver.com/v1/search/cafearticle.json?query=" + id,
                "https://openapi.naver.com/v1/search/cafearticle.json?query=" + nickname,
                "https://openapi.naver.com/v1/search/cafearticle.json?query=" + "\"" + phone + "\"",
                "https://openapi.naver.com/v1/search/cafearticle.json?query=" + "\"" + email + "\"",
                "https://openapi.naver.com/v1/search/cafearticle.json?query=" + name + " " + "\"" + phone + "\"",
                "https://openapi.naver.com/v1/search/cafearticle.json?query=" + name + " " + "\"" + email + "\"",
                "https://openapi.naver.com/v1/search/cafearticle.json?query=" + name + " " + "\"" + id + "\"",
                "https://openapi.naver.com/v1/search/cafearticle.json?query=" + nickname + " " + "\"" + phone + "\"",
                "https://openapi.naver.com/v1/search/cafearticle.json?query=" + nickname + " " + "\"" + email + "\"",
                "https://openapi.naver.com/v1/search/cafearticle.json?query=" + nickname + " " + "\"" + id + "\"",
                "https://openapi.naver.com/v1/search/cafearticle.json?query=" + "\"" + phone + "\"" + " " + "\"" + id + "\"",
                "https://openapi.naver.com/v1/search/cafearticle.json?query=" + "\"" + email + "\"" + " " + "\"" + id + "\""
        );
        List<String> overlapCheck = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);

        for (String apiUrl : apiUrls) {
            JsonNode node = getNode(apiUrl, headers);
            if (node == null) {
                return result;
            }
            JsonNode totalJson = node.get("total");
            int totalPaper = totalJson.intValue();
            if (totalPaper < 100) {
                for (JsonNode item : node.get("items")) {
                    String link = item.get("link").textValue();

                    if (!overlapCheck.contains(link)) {
                        String title = item.get("title").textValue();
                        String description = item.get("description").textValue();
                        result.addContent(removeTag(totalPaper + title + " " + description), link);
                    }
                    overlapCheck.add(link);
                }
            }
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
            String url_temp = "";
            result.addContent(subject.text() + " " + date.text(), url_temp);
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

        total_num_json = node.get("meta");
        total_paper = total_num_json.get("total_count").intValue();

        if(total_paper < 100){
            for (JsonNode document : node.get("documents")) {
                String link = document.get("url").textValue();

                if(!overlap_Check.contains(link)){
                    String title = document.get("title").textValue();
                    String contents = document.get("contents").textValue();
                    result.addContent(removeTag(title + " " + contents), link);
                }
                overlap_Check.add(link);
            }
        }

        // Name, E-mail
        urlApi = "https://dapi.kakao.com/v2/search/web?query=" + naverName + " " + "\"" + naverEmail + "\"";

        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_num_json = node.get("meta");
        total_paper = total_num_json.get("total_count").intValue();

        if(total_paper < 100){
            for (JsonNode document : node.get("documents")) {
                String link = document.get("url").textValue();

                if(!overlap_Check.contains(link)){
                    String title = document.get("title").textValue();
                    String contents = document.get("contents").textValue();
                    result.addContent(removeTag(title + " " + contents), link);
                }
                overlap_Check.add(link);
            }
        }

        // Name, ID
        urlApi = "https://dapi.kakao.com/v2/search/web?query=" + naverName + " " + "\"" + id + "\"";

        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_num_json = node.get("meta");
        total_paper = total_num_json.get("total_count").intValue();

        if(total_paper < 100){
            for (JsonNode document : node.get("documents")) {
                String link = document.get("url").textValue();

                if(!overlap_Check.contains(link)){
                    String title = document.get("title").textValue();
                    String contents = document.get("contents").textValue();
                    result.addContent(removeTag(title + " " + contents), link);
                }
                overlap_Check.add(link);
            }
        }

        // Nickname + Phone number
        urlApi = "https://dapi.kakao.com/v2/search/web?query=" + naverNickname + " " + "\"" + naverPhonNumber + "\"";

        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_num_json = node.get("meta");
        total_paper = total_num_json.get("total_count").intValue();

        if(total_paper < 100){
            for (JsonNode document : node.get("documents")) {
                String link = document.get("url").textValue();

                if(!overlap_Check.contains(link)){
                    String title = document.get("title").textValue();
                    String contents = document.get("contents").textValue();
                    result.addContent(removeTag(title + " " + contents), link);
                }
                overlap_Check.add(link);
            }
        }

        // Nickname + E-mail
        urlApi = "https://dapi.kakao.com/v2/search/web?query=" + naverNickname + " " + "\"" + naverEmail + "\"";

        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_num_json = node.get("meta");
        total_paper = total_num_json.get("total_count").intValue();

        if(total_paper < 100){
            for (JsonNode document : node.get("documents")) {
                String link = document.get("url").textValue();

                if(!overlap_Check.contains(link)){
                    String title = document.get("title").textValue();
                    String contents = document.get("contents").textValue();
                    result.addContent(removeTag(title + " " + contents), link);
                }
                overlap_Check.add(link);
            }
        }

        // Nickname + ID
        urlApi = "https://dapi.kakao.com/v2/search/web?query=" + naverNickname + " " + "\"" + id + "\"";

        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_num_json = node.get("meta");
        total_paper = total_num_json.get("total_count").intValue();

        if(total_paper < 100){
            for (JsonNode document : node.get("documents")) {
                String link = document.get("url").textValue();

                if(!overlap_Check.contains(link)){
                    String title = document.get("title").textValue();
                    String contents = document.get("contents").textValue();
                    result.addContent(removeTag(title + " " + contents), link);
                }
                overlap_Check.add(link);
            }
        }

        // Phone Number + ID
        urlApi = "https://dapi.kakao.com/v2/search/web?query=" + "\"" + naverPhonNumber + "\"" + " " + "\"" + id + "\"";

        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_num_json = node.get("meta");
        total_paper = total_num_json.get("total_count").intValue();

        if(total_paper < 100){
            for (JsonNode document : node.get("documents")) {
                String link = document.get("url").textValue();

                if(!overlap_Check.contains(link)){
                    String title = document.get("title").textValue();
                    String contents = document.get("contents").textValue();
                    result.addContent(removeTag(title + " " + contents), link);
                }
                overlap_Check.add(link);
            }
        }

        // E-mail + ID
        urlApi = "https://dapi.kakao.com/v2/search/web?query=" + "\"" + naverEmail + "\"" + " " + "\"" + id + "\"";

        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_num_json = node.get("meta");
        total_paper = total_num_json.get("total_count").intValue();

        if(total_paper < 100){
            for (JsonNode document : node.get("documents")) {
                String link = document.get("url").textValue();

                if(!overlap_Check.contains(link)){
                    String title = document.get("title").textValue();
                    String contents = document.get("contents").textValue();
                    result.addContent(removeTag(title + " " + contents), link);
                }
                overlap_Check.add(link);
            }
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
            String url_temp = url;
            result.addContent(contentElement.text() + " " + fUrlElement.text(), url_temp);
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
            String url_temp = url;
            result.addContent(dateElement.text() + " " + titleElement.text() + " " + contentElement.text(),url_temp);
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
            String url_temp = url;
            result.addContent(dateElement.text() + " " + contentElement.text(),url_temp);
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
            String url_temp = url;
            result.addContent(time.text() + " " + title.text(),url_temp);
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
        Iterator<Element> rurl = element.select("link_article._linkList").iterator();

        while (date.hasNext()) {
            Element dateElement = date.next();
            Element contentElementQ = contentQ.next();
            Element contentElementA = contentA.next();
            Element urlElement = contentA.next();
            //String urll = element.attr('data-detail-url');

            String url_temp = url;
            result.addContent(dateElement.text() + " Q:" + contentElementQ.text() + " A:" + contentElementA.text(),url_temp);
        }
        return result;
    }

    private SearchResultDTO crawlNaverSearch(String id, String naverName, String naverNickname, String naverPhonNumber, String naverEmail) {
        String url = "https://search.naver.com/search.naver?where=article&query=" + id;
        SearchResultDTO result = new SearchResultDTO("네이버 검색", url);


        //중복제거

        List<String> overlap_Check = new ArrayList<String>();


        //Search ID

        String urlApi = "https://openapi.naver.com/v1/search/cafearticle.json?query=" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);

        JsonNode node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }
        JsonNode total_json = node.get("total");
        int total_paper = total_json.intValue();



        if(total_paper < 100){
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if(!overlap_Check.contains(link)){
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(total_paper + title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }



        //Search NickName

        urlApi = "https://openapi.naver.com/v1/search/cafearticle.json?query=" + naverNickname;
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();



        if(total_paper < 100){
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if(!overlap_Check.contains(link)){
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }



        //Search PhoneNumber

        urlApi = "https://openapi.naver.com/v1/search/cafearticle.json?query=" + "\"" + naverPhonNumber + "\"";
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if (!overlap_Check.contains(link)) {
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }



        //Search Email

        urlApi = "https://openapi.naver.com/v1/search/cafearticle.json?query=" + "\"" + naverEmail + "\"";
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if(!overlap_Check.contains(link)){
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }




        //Search ID + PhoneNumber

        urlApi = "https://openapi.naver.com/v1/search/cafearticle.json?query=" + id + " " + "\"" + naverPhonNumber + "\"";
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if(!overlap_Check.contains(link)){
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }



        //Search ID + Email

        urlApi = "https://openapi.naver.com/v1/search/cafearticle.json?query=" + id + " " + "\"" + naverEmail + "\"";
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if (!overlap_Check.contains(link)) {
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }



        //Search Name + PhonNumber

        urlApi = "https://openapi.naver.com/v1/search/cafearticle.json?query=" + naverName + " " + "\"" + naverPhonNumber + "\"";
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if(!overlap_Check.contains(link)){
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }


        //Search Name + Email

        urlApi = "https://openapi.naver.com/v1/search/cafearticle.json?query=" + naverName + " " + "\"" + naverEmail + "\"";
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if (!overlap_Check.contains(link)) {
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }

        //Search Name + ID

        urlApi = "https://openapi.naver.com/v1/search/cafearticle.json?query=" + naverName + " " + id;
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if (!overlap_Check.contains(link)) {
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }

        //Search NickName + ID

        urlApi = "https://openapi.naver.com/v1/search/cafearticle.json?query=" + naverNickname + " " + id;
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if (!overlap_Check.contains(link)) {
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }

        //Search NickName + Email

        urlApi = "https://openapi.naver.com/v1/search/cafearticle.json?query=" + naverNickname + " " + "\"" + naverEmail + "\"";

        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if (!overlap_Check.contains(link)) {
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }

        //Search NickName + Phone

        urlApi = "https://openapi.naver.com/v1/search/cafearticle.json?query=" + naverNickname + " " + "\"" + naverPhonNumber + "\"";

        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if (!overlap_Check.contains(link)) {
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }

        //Web Site Search!!

        //Search ID

        urlApi = "https://openapi.naver.com/v1/search/webkr.json?query=" + id;
        headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);

        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }
        total_json = node.get("total");
        total_paper = total_json.intValue();



        if(total_paper < 100){
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if(!overlap_Check.contains(link)){
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }



        //Search NickName

        urlApi = "https://openapi.naver.com/v1/search/webkr.json?query=" + naverNickname;
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();



        if(total_paper < 100){
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if(!overlap_Check.contains(link)){
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }



        //Search PhoneNumber

        urlApi = "https://openapi.naver.com/v1/search/webkr.json?query=" + "\"" + naverPhonNumber + "\"";
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if (!overlap_Check.contains(link)) {
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }



        //Search Email

        urlApi = "https://openapi.naver.com/v1/search/webkr.json?query=" + "\"" + naverEmail + "\"";
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if(!overlap_Check.contains(link)){
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }




        //Search ID + PhoneNumber

        urlApi = "https://openapi.naver.com/v1/search/webkr.json?query=" + id + " " + "\"" + naverPhonNumber + "\"";
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if(!overlap_Check.contains(link)){
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }



        //Search ID + Email

        urlApi = "https://openapi.naver.com/v1/search/webkr.json?query=" + id + " " + "\"" + naverEmail + "\"";
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if (!overlap_Check.contains(link)) {
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }



        //Search Name + PhonNumber

        urlApi = "https://openapi.naver.com/v1/search/webkr.json?query=" + naverName + " " + "\"" + naverPhonNumber + "\"";
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if(!overlap_Check.contains(link)){
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }


        //Search Name + Email

        urlApi = "https://openapi.naver.com/v1/search/webkr.json?query=" + naverName + " " + "\"" + naverEmail + "\"";
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if (!overlap_Check.contains(link)) {
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }

        //Search Name + ID

        urlApi = "https://openapi.naver.com/v1/search/webkr.json?query=" + naverName + " " + id;
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if (!overlap_Check.contains(link)) {
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }

        //Search NickName + ID

        urlApi = "https://openapi.naver.com/v1/search/webkr.json?query=" + naverNickname + " " + id;
        //headers = new HttpHeaders();
        //headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        //headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if (!overlap_Check.contains(link)) {
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }

        //Search NickName + Email

        urlApi = "https://openapi.naver.com/v1/search/webkr.json?query=" + naverNickname + " " + "\"" + naverEmail + "\"";

        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if (!overlap_Check.contains(link)) {
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
        }

        //Search NickName + Phone

        urlApi = "https://openapi.naver.com/v1/search/webkr.json?query=" + naverNickname + " " + "\"" + naverPhonNumber + "\"";

        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_json = node.get("total");
        total_paper = total_json.intValue();

        if(total_paper < 100) {
            for (JsonNode item : node.get("items")) {
                String link = item.get("link").textValue();

                if (!overlap_Check.contains(link)) {
                    String title = item.get("title").textValue();
                    String description = item.get("description").textValue();
                    result.addContent(removeTag(title + " " + description),link);
                }
                overlap_Check.add(link);
            }
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
            String url_temp = url;
            result.addContent(subject.text() + " " + date.text(), url_temp);
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
            String url_temp = url;
            result.addContent(content.text(),url_temp);
        }
        return result;
    }

    private SearchResultDTO crawlGoogleSearch(String id, String naverName, String naverNickname, String naverPhonNumber, String naverEmail){
        //Search ID
        String url = "https://www.google.com/search?newwindow=1&ei=sfheXablEo3nwQO1yK2gCg&q=" + id;
        SearchResultDTO result = new SearchResultDTO("Google 검색", url);

        //중복제거

        List<String> overlap_Check = new ArrayList<String>();

        Document doc = null;        //Document에는 페이지의 전체 소스가 저장된다

        try {
            doc = Jsoup.connect(url).get();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        Elements element = doc.select("div.bkWMgd");

        Iterator<Element> ie1 = element.select("h3.LC20lb").iterator();
        Iterator<Element> ie2 = element.select("span.st").iterator();
        Iterator<Element> ie5 = element.select(".r").iterator();

        List<String> CrawlURL = new ArrayList<String>();

        while (ie1.hasNext()) {
            String contents = ie1.next().text() + " "  + ie2.next().text();
            Element urlElement = ie5.next().selectFirst(".r").child(0);
            String href = urlElement.attr("href");
            CrawlURL.add(href);

            if(!overlap_Check.contains(href)){
                result.addContent(contents , href);
            }
            overlap_Check.add(href);
        }

        //Search Nickname
        url = "https://www.google.com/search?newwindow=1&ei=sfheXablEo3nwQO1yK2gCg&q=" + naverNickname;

        doc = null;        //Document에는 페이지의 전체 소스가 저장된다

        try {
            doc = Jsoup.connect(url).get();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        element = doc.select("div.bkWMgd");

        ie1 = element.select("h3.LC20lb").iterator();
        ie2 = element.select("span.st").iterator();
        ie5 = element.select(".r").iterator();

        CrawlURL = new ArrayList<String>();

        while (ie1.hasNext()) {
            String contents = ie1.next().text() + " "  + ie2.next().text();
            Element urlElement = ie5.next().selectFirst(".r").child(0);
            String href = urlElement.attr("href");
            CrawlURL.add(href);

            if(!overlap_Check.contains(href)){
                result.addContent(contents , href);
            }
            overlap_Check.add(href);
        }

        //Search Phone number
        url = "https://www.google.com/search?newwindow=1&ei=sfheXablEo3nwQO1yK2gCg&q=" + "\"" + naverPhonNumber + "\"";

        doc = null;        //Document에는 페이지의 전체 소스가 저장된다

        try {
            doc = Jsoup.connect(url).get();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        element = doc.select("div.bkWMgd");

        ie1 = element.select("h3.LC20lb").iterator();
        ie2 = element.select("span.st").iterator();
        ie5 = element.select(".r").iterator();

        CrawlURL = new ArrayList<String>();

        while (ie1.hasNext()) {
            String contents = ie1.next().text() + " "  + ie2.next().text();
            Element urlElement = ie5.next().selectFirst(".r").child(0);
            String href = urlElement.attr("href");
            CrawlURL.add(href);

            if(!overlap_Check.contains(href)){
                result.addContent(contents , href);
            }
            overlap_Check.add(href);
        }

        //Search E-mail
        url = "https://www.google.com/search?newwindow=1&ei=sfheXablEo3nwQO1yK2gCg&q=" + "\"" + naverEmail + "\"";

        doc = null;        //Document에는 페이지의 전체 소스가 저장된다

        try {
            doc = Jsoup.connect(url).get();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        element = doc.select("div.bkWMgd");

        ie1 = element.select("h3.LC20lb").iterator();
        ie2 = element.select("span.st").iterator();
        ie5 = element.select(".r").iterator();

        CrawlURL = new ArrayList<String>();

        while (ie1.hasNext()) {
            String contents = ie1.next().text() + " "  + ie2.next().text();
            Element urlElement = ie5.next().selectFirst(".r").child(0);
            String href = urlElement.attr("href");
            CrawlURL.add(href);

            if(!overlap_Check.contains(href)){
                result.addContent(contents , href);
            }
            overlap_Check.add(href);
        }

        //Search Name + Phone Number
        url = "https://www.google.com/search?newwindow=1&ei=sfheXablEo3nwQO1yK2gCg&q=" + "\"" + naverName + "\"" + "\"" + naverPhonNumber + "\"";

        doc = null;        //Document에는 페이지의 전체 소스가 저장된다

        try {
            doc = Jsoup.connect(url).get();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        element = doc.select("div.bkWMgd");

        ie1 = element.select("h3.LC20lb").iterator();
        ie2 = element.select("span.st").iterator();
        ie5 = element.select(".r").iterator();

        CrawlURL = new ArrayList<String>();

        while (ie1.hasNext()) {
            String contents = ie1.next().text() + " "  + ie2.next().text();
            Element urlElement = ie5.next().selectFirst(".r").child(0);
            String href = urlElement.attr("href");
            CrawlURL.add(href);

            if(!overlap_Check.contains(href)){
                result.addContent(contents , href);
            }
            overlap_Check.add(href);
        }

        //Search Name + Email
        url = "https://www.google.com/search?newwindow=1&ei=sfheXablEo3nwQO1yK2gCg&q=" + "\"" + naverName + "\"" + "\"" + naverEmail + "\"";

        doc = null;        //Document에는 페이지의 전체 소스가 저장된다

        try {
            doc = Jsoup.connect(url).get();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        element = doc.select("div.bkWMgd");

        ie1 = element.select("h3.LC20lb").iterator();
        ie2 = element.select("span.st").iterator();
        ie5 = element.select(".r").iterator();

        CrawlURL = new ArrayList<String>();

        while (ie1.hasNext()) {
            String contents = ie1.next().text() + " "  + ie2.next().text();
            Element urlElement = ie5.next().selectFirst(".r").child(0);
            String href = urlElement.attr("href");
            CrawlURL.add(href);

            if(!overlap_Check.contains(href)){
                result.addContent(contents , href);
            }
            overlap_Check.add(href);
        }

        //Search Name + ID
        url = "https://www.google.com/search?newwindow=1&ei=sfheXablEo3nwQO1yK2gCg&q=" + "\"" + naverName + "\"" + "\"" + id + "\"";

        doc = null;        //Document에는 페이지의 전체 소스가 저장된다

        try {
            doc = Jsoup.connect(url).get();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        element = doc.select("div.bkWMgd");

        ie1 = element.select("h3.LC20lb").iterator();
        ie2 = element.select("span.st").iterator();
        ie5 = element.select(".r").iterator();

        CrawlURL = new ArrayList<String>();

        while (ie1.hasNext()) {
            String contents = ie1.next().text() + " "  + ie2.next().text();
            Element urlElement = ie5.next().selectFirst(".r").child(0);
            String href = urlElement.attr("href");
            CrawlURL.add(href);

            if(!overlap_Check.contains(href)){
                result.addContent(contents , href);
            }
            overlap_Check.add(href);
        }

        //Search Nickname + Phone number
        url = "https://www.google.com/search?newwindow=1&ei=sfheXablEo3nwQO1yK2gCg&q=" + "\"" + naverNickname + "\"" + "\"" + naverPhonNumber + "\"";

        doc = null;        //Document에는 페이지의 전체 소스가 저장된다

        try {
            doc = Jsoup.connect(url).get();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        element = doc.select("div.bkWMgd");

        ie1 = element.select("h3.LC20lb").iterator();
        ie2 = element.select("span.st").iterator();
        ie5 = element.select(".r").iterator();

        CrawlURL = new ArrayList<String>();

        while (ie1.hasNext()) {
            String contents = ie1.next().text() + " "  + ie2.next().text();
            Element urlElement = ie5.next().selectFirst(".r").child(0);
            String href = urlElement.attr("href");
            CrawlURL.add(href);

            if(!overlap_Check.contains(href)){
                result.addContent(contents , href);
            }
            overlap_Check.add(href);
        }

        //Search NicName + Email
        url = "https://www.google.com/search?newwindow=1&ei=sfheXablEo3nwQO1yK2gCg&q=" + "\"" + naverNickname + "\"" + "\"" + naverEmail + "\"";

        doc = null;        //Document에는 페이지의 전체 소스가 저장된다

        try {
            doc = Jsoup.connect(url).get();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        element = doc.select("div.bkWMgd");

        ie1 = element.select("h3.LC20lb").iterator();
        ie2 = element.select("span.st").iterator();
        ie5 = element.select(".r").iterator();

        CrawlURL = new ArrayList<String>();

        while (ie1.hasNext()) {
            String contents = ie1.next().text() + " "  + ie2.next().text();
            Element urlElement = ie5.next().selectFirst(".r").child(0);
            String href = urlElement.attr("href");
            CrawlURL.add(href);

            if(!overlap_Check.contains(href)){
                result.addContent(contents , href);
            }
            overlap_Check.add(href);
        }

        //Search Nicname + id
        url = "https://www.google.com/search?newwindow=1&ei=sfheXablEo3nwQO1yK2gCg&q=" + "\"" + naverNickname + "\"" + "\"" + id + "\"";

        doc = null;        //Document에는 페이지의 전체 소스가 저장된다

        try {
            doc = Jsoup.connect(url).get();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        element = doc.select("div.bkWMgd");

        ie1 = element.select("h3.LC20lb").iterator();
        ie2 = element.select("span.st").iterator();
        ie5 = element.select(".r").iterator();

        CrawlURL = new ArrayList<String>();

        while (ie1.hasNext()) {
            String contents = ie1.next().text() + " "  + ie2.next().text();
            Element urlElement = ie5.next().selectFirst(".r").child(0);
            String href = urlElement.attr("href");
            CrawlURL.add(href);

            if(!overlap_Check.contains(href)){
                result.addContent(contents , href);
            }
            overlap_Check.add(href);
        }

        //Search ID + Phone number
        url = "https://www.google.com/search?newwindow=1&ei=sfheXablEo3nwQO1yK2gCg&q=" + "\"" + id + "\"" + "\"" + naverPhonNumber + "\"";

        doc = null;        //Document에는 페이지의 전체 소스가 저장된다

        try {
            doc = Jsoup.connect(url).get();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        element = doc.select("div.bkWMgd");

        ie1 = element.select("h3.LC20lb").iterator();
        ie2 = element.select("span.st").iterator();
        ie5 = element.select(".r").iterator();

        CrawlURL = new ArrayList<String>();

        while (ie1.hasNext()) {
            String contents = ie1.next().text() + " "  + ie2.next().text();
            Element urlElement = ie5.next().selectFirst(".r").child(0);
            String href = urlElement.attr("href");
            CrawlURL.add(href);

            if(!overlap_Check.contains(href)){
                result.addContent(contents , href);
            }
            overlap_Check.add(href);
        }

        //Search ID + Email
        url = "https://www.google.com/search?newwindow=1&ei=sfheXablEo3nwQO1yK2gCg&q=" + "\"" + id + "\"" + "\"" + naverEmail + "\"";

        doc = null;        //Document에는 페이지의 전체 소스가 저장된다

        try {
            doc = Jsoup.connect(url).get();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        element = doc.select("div.bkWMgd");

        ie1 = element.select("h3.LC20lb").iterator();
        ie2 = element.select("span.st").iterator();
        ie5 = element.select(".r").iterator();

        CrawlURL = new ArrayList<String>();

        while (ie1.hasNext()) {
            String contents = ie1.next().text() + " "  + ie2.next().text();
            Element urlElement = ie5.next().selectFirst(".r").child(0);
            String href = urlElement.attr("href");
            CrawlURL.add(href);

            if(!overlap_Check.contains(href)){
                result.addContent(contents , href);
            }
            overlap_Check.add(href);
        }

        return result;
    }
}
