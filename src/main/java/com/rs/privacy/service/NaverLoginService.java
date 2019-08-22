package com.rs.privacy.service;

import com.rs.privacy.model.NaverLoginTokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.security.SecureRandom;

@Service
public class NaverLoginService {

    private final static String CLIENT_ID = "iiTpSGlHgIkuycXWTh3N";
    private final static String CLIENT_SECRET = "34ZpK2RcPz";

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    public String getAuthorizeUrl(String redirectUri) {
        String state = generateState();

        return UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", state)
                .build().toUriString();
    }

    public NaverLoginTokenDTO getToken(String code, String state) {
        String url = UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("client_secret", CLIENT_SECRET)
                .queryParam("code", code)
                .queryParam("state", state)
                .build().toUriString();

        RestTemplate restTemplate = restTemplateBuilder.build();

        return restTemplate.getForObject(url, NaverLoginTokenDTO.class);
    }

    private String generateState() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}
