package com.rs.privacy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rs.privacy.model.SearchDTO;
import com.rs.privacy.model.SearchTokenDTO;
import com.rs.privacy.model.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SearchService {

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    public List<SearchResult> search(SearchTokenDTO searchTokenDTO) {
        SearchDTO searchDTO = getSearchDTO(searchTokenDTO);

        // TODO: crawl

        return null;
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
}
