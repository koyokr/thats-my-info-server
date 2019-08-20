package com.rs.privacy.web;

import com.rs.privacy.model.NaverLoginTokenDTO;
import com.rs.privacy.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.security.SecureRandom;


@RestController
@RequestMapping("/nvlogin")
public class NaverLoginController {
    private final static String CLIENT_ID = "iiTpSGlHgIkuycXWTh3N";
    private final static String CLIENT_SECRET = "34ZpK2RcPz";

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    private String generateState() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    @GetMapping("/auth")
    public RedirectView auth() {
        String redirectUri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .replacePath("/nvlogin/callback").build().toUriString();

        String state = generateState();
        String url = UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", state)
                .build().toUriString();

        return new RedirectView(url);
    }

    @GetMapping("/callback")
    public RedirectView callback(@RequestParam String code, @RequestParam String state) {
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
            return new RedirectView("./failure");
        }
        return new RedirectView("./success#" + token.getAccessToken());
    }

    @GetMapping("/success")
    public ResponseEntity<Void> success() {
        return ResponseUtils.makeResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/failure")
    public ResponseEntity<Void> failure() {
        return ResponseUtils.makeResponseEntity(HttpStatus.OK);
    }
}
