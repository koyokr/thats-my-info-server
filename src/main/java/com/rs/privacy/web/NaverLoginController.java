package com.rs.privacy.web;

import com.rs.privacy.model.NaverLoginTokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.security.SecureRandom;


@RestController
@RequestMapping("/nvlogin")
public class NaverLoginController {
    private final static String CLIENT_ID = "iiTpSGlHgIkuycXWTh3N";
    private final static String CLIENT_SECRET = "34ZpK2RcPz";
    private final static String REDIRECT_URI = "http%3A%2F%2Flocalhost%3A8080%2Fnvlogin%2Fcallback";

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    private String generateState() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    @GetMapping("/auth")
    public RedirectView auth() {
        String state = generateState();
        String url = UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("state", state)
                .build().toUriString();

        return new RedirectView(url);
    }

    @GetMapping("/callback")
    public String callback(@RequestParam String code, @RequestParam String state) {
        String url = UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("client_secret", CLIENT_SECRET)
                .queryParam("code", code)
                .queryParam("state", state)
                .build().toUriString();

        RestTemplate restTemplate = restTemplateBuilder.build();
        NaverLoginTokenDTO token = restTemplate.getForObject(url, NaverLoginTokenDTO.class);

        if (token.getError() != null) {
            return null;
        }
        return token.getAccessToken();
    }
}
