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
        String naverName = personDTO.getName();
        String naverNickname= personDTO.getNickname();
        String naverPhonNumber= personDTO.getPhone();
        String naverEmail= personDTO.getEmail();

//        String naverId = "everpall";
//        String naverName = "박성현";
//        String naverNickname= "everpall";
//        String naverPhonNumber= "010-4012-7157";
//        String naverEmail= "everpall@naver.com";

        String url;

        List<SearchResultDTO> resultList = new ArrayList<>();
        resultList.add(crawlBing(naverId, naverName, naverNickname, naverPhonNumber,naverEmail));
        resultList.add(crawlClien(naverId));
        resultList.add(crawlDaumCafe(naverId, naverName, naverNickname, naverPhonNumber,naverEmail));
        resultList.add(crawlDaumPaper(naverId, naverName, naverNickname, naverPhonNumber,naverEmail));
        resultList.add(crawlDaumSite(naverId));
        resultList.add(crawlDC(naverId));
        resultList.add(crawlIlbe(naverId));
        resultList.add(crawlNaverCafe(naverId, "22830216"));
        resultList.add(crawlNaverCafe(naverId, "10050146"));
        resultList.add(crawlNaverCafe(naverId, "10050813"));
        resultList.add(crawlNaverCafe(naverId, "11262350"));
        resultList.add(crawlNaverKin(naverId));
        resultList.add(crawlNaverSearch(naverId,  naverName,naverNickname, naverPhonNumber,naverEmail));
        resultList.add(crawlTodayHumor(naverId));
        resultList.add(crawlTwitter(naverId));
        //resultList.add(crawlGoogleSearch(naverId, naverName, naverNickname, naverPhonNumber,naverEmail));

        return resultList;
    }

    public TotalSearchResultDTO search(PerosnTokenDTO perosnTokenDTO) {
        PersonDTO personDTO = getSearchDTO(perosnTokenDTO);
        if (personDTO == null) {
            return null;
        }
        String naverId = personDTO.getNaverId();
        String naverName = personDTO.getName();
        String naverNickname= personDTO.getNickname();
        String naverPhonNumber= personDTO.getPhone();
        String naverEmail= personDTO.getEmail();

//        String naverId = "everpall";
//        String naverName = "박성현";
//        String naverNickname= "everpall";
//        String naverPhonNumber= "010-4012-7157";
//        String naverEmail= "everpall@naver.com";

        if (!existsNaverId(naverId)) {
            return null;
        }

        List<SearchResultDTO> searchResults = new ArrayList<>();
        searchResults.add(crawlBing(naverId, naverName, naverNickname, naverPhonNumber,naverEmail));
        searchResults.add(crawlClien(naverId));
        searchResults.add(crawlDaumCafe(naverId, naverName, naverNickname, naverPhonNumber,naverEmail));
        searchResults.add(crawlDaumPaper(naverId, naverName, naverNickname, naverPhonNumber,naverEmail));
        searchResults.add(crawlDaumSite(naverId));
        searchResults.add(crawlDC(naverId));
        searchResults.add(crawlIlbe(naverId));
        searchResults.add(crawlNaverCafe(naverId, "22830216"));
        searchResults.add(crawlNaverCafe(naverId, "10050146"));
        searchResults.add(crawlNaverCafe(naverId, "10050813"));
        searchResults.add(crawlNaverCafe(naverId, "11262350"));
        searchResults.add(crawlNaverKin(naverId));
        searchResults.add(crawlNaverSearch(naverId, naverName, naverNickname, naverPhonNumber,naverEmail));
        searchResults.add(crawlTodayHumor(naverId));
        searchResults.add(crawlTwitter(naverId));
        //searchResults.add(crawlGoogleSearch(naverId, naverName, naverNickname, naverPhonNumber,naverEmail));

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

    private SearchResultDTO crawlBing(String id, String naverName, String naverNickname, String naverPhonNumber, String naverEmail) {
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
            String url_temp = "";
            result.addContent(name + " " + snippet, url_temp);
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
            String url_temp = "";
            result.addContent(dateElement.text() + " " + titleElement.text(), url_temp);
        }
        return result;
    }

    private SearchResultDTO crawlDaumCafe(String id, String naverName, String naverNickname, String naverPhonNumber, String naverEmail) {
        String url = "http://search.daum.net/search?w=cafe&q=" + id;
        SearchResultDTO result = new SearchResultDTO("다음 카페", url);

        //중복제거

        List<String> overlap_Check = new ArrayList<String>();

        // ID
        String urlApi = "https://dapi.kakao.com/v2/search/cafe?query=" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_REST_API_KEY);

        JsonNode node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        JsonNode total_num_json = node.get("meta");
        int total_paper = total_num_json.get("total_count").intValue();

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



        // Nick name
        urlApi = "https://dapi.kakao.com/v2/search/cafe?query=" + naverNickname;

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

        // Phone Number
        urlApi = "https://dapi.kakao.com/v2/search/cafe?query=" + "\"" + naverPhonNumber + "\"";;

        node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        total_num_json = node.get("meta");
        total_paper = total_num_json.get("total_count").intValue();

        if(total_paper < 10){
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

        // E-mail

        // Name, Phone number
        urlApi = "https://dapi.kakao.com/v2/search/cafe?query=" + naverName + " " + "\"" + naverPhonNumber + "\"";

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

        // Name, E-mail
        urlApi = "https://dapi.kakao.com/v2/search/cafe?query=" + naverName + " " + "\"" + naverEmail + "\"";

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
        urlApi = "https://dapi.kakao.com/v2/search/cafe?query=" + naverName + " " + "\"" + id + "\"";

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
        urlApi = "https://dapi.kakao.com/v2/search/cafe?query=" + naverNickname + " " + "\"" + naverPhonNumber + "\"";

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
        urlApi = "https://dapi.kakao.com/v2/search/cafe?query=" + naverNickname + " " + "\"" + naverEmail + "\"";

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
        urlApi = "https://dapi.kakao.com/v2/search/cafe?query=" + naverNickname + " " + "\"" + id + "\"";

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
        urlApi = "https://dapi.kakao.com/v2/search/cafe?query=" + "\"" + naverPhonNumber + "\"" + " " + "\"" + id + "\"";

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
        urlApi = "https://dapi.kakao.com/v2/search/cafe?query=" + "\"" + naverEmail + "\"" + " " + "\"" + id + "\"";

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

    private SearchResultDTO crawlDaumPaper(String id, String naverName, String naverNickname, String naverPhonNumber, String naverEmail) {
        String url = "https://search.daum.net/search?w=web&q=" + id;
        SearchResultDTO result = new SearchResultDTO("다음 웹문서 검색", url);

        //중복제거

        List<String> overlap_Check = new ArrayList<String>();

        // ID
        String urlApi = "https://dapi.kakao.com/v2/search/web?query=" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_REST_API_KEY);

        JsonNode node = getNode(urlApi, headers);
        if (node == null) {
            return result;
        }

        JsonNode total_num_json = node.get("meta");
        int total_paper = total_num_json.get("total_count").intValue();


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



        // Nick name
        urlApi = "https://dapi.kakao.com/v2/search/web?query=" + naverNickname;

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

        // Phone Number
        urlApi = "https://dapi.kakao.com/v2/search/web?query=" + "\"" + naverPhonNumber + "\"";;

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

        // E-mail
        urlApi = "https://dapi.kakao.com/v2/search/web?query=" + "\"" + naverEmail + "\"";;

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

        // Name, Phone number
        urlApi = "https://dapi.kakao.com/v2/search/web?query=" + naverName + " " + "\"" + naverPhonNumber + "\"";

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
            String url_temp = "";
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
            String url_temp = "";
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

        while (date.hasNext()) {
            Element dateElement = date.next();
            Element contentElementQ = contentQ.next();
            Element contentElementA = contentA.next();
            String url_temp = "";
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
        for (Element content : element.select("div.js-tweet-text-container")) {
            String url_temp = "";
            result.addContent(content.text(),url_temp);
        }
        return result;
    }


}
