package com.rs.privacy.web;

import com.rs.privacy.model.NaverLoginTokenDTO;
import com.rs.privacy.service.NaverLoginService;
import com.rs.privacy.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;


@RestController
@RequestMapping("/nvlogin")
public class NaverLoginController {

    @Autowired
    private NaverLoginService naverLoginService;

    @GetMapping("/auth")
    public RedirectView auth() {
        String redirectUri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .replacePath("/nvlogin/callback")
                .build().toUriString();
        String url = naverLoginService.getAuthorizeUrl(redirectUri);

        return new RedirectView(url);
    }

    @GetMapping("/callback")
    public RedirectView callback(@RequestParam String code, @RequestParam String state) {
        NaverLoginTokenDTO token = naverLoginService.getToken(code, state);

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
