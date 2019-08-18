package com.rs.privacy.web;

import com.rs.privacy.model.NaverLoginTokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;


@RestController
@RequestMapping("/nvlogin")
public class NaverLoginController {
    private final static String CLIENT_ID = "iiTpSGlHgIkuycXWTh3N";
    private final static String CLIENT_SECRET = "34ZpK2RcPz";
    private final static String REDIRECT_URI = "http%3A%2F%2Flocalhost%3A8080%2Fnvlogin%2Fcallback";
    private final static String SESSION_STATE = "oauth_state";
    private final static String PROFILE_API_URL = "https://openapi.naver.com/v1/nid/me";

    @Autowired
    RestTemplateBuilder restTemplateBuilder;


    public String generateState() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    @GetMapping("/auth-url")
    public String authUrl(HttpSession session) {
        String state = generateState();
        session.setAttribute(SESSION_STATE, state);

        String url = UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("state", state)
                .build().toUriString();

        return url;
    }

    @GetMapping("/callback")
    public String callback(@RequestParam String code, @RequestParam String state, HttpSession session) {
        String sessionState = session.getAttribute(SESSION_STATE).toString();

        if (!StringUtils.pathEquals(sessionState, state)) {
            return "fail";
        }

        URI uri = UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("client_secret", CLIENT_SECRET)
                .queryParam("code", code)
                .queryParam("state", state)
                .build().toUri();

        RestTemplate restTemplate = restTemplateBuilder.build();
        NaverLoginTokenDTO token = restTemplate.getForObject(uri, NaverLoginTokenDTO.class);
        String accessToken = token.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String result = restTemplate.postForObject(PROFILE_API_URL, entity, String.class);
        return result;
    }
}
