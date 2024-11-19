package com.banghyang.oauth.infra.presentation;

import com.banghyang.oauth.application.OauthService;
import com.banghyang.oauth.domain.OauthServerType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/oauth")
@RestController
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    /**
     * 서비스를 통해 생성된 AuthCode 발급받는 url 로 사용자를 리다이렉트 시킴
     * @param oauthServerType
     * @param response
     * @return
     */
    @SneakyThrows
    @GetMapping("/{oauthServerType}")
    ResponseEntity<Void> redirectAuthCodeRequestUrl(
            @PathVariable OauthServerType oauthServerType,
            HttpServletResponse response
    ) {
        String redirectUrl = oauthService.getAuthCodeRequestUrl(oauthServerType);
        response.sendRedirect(redirectUrl);
        return ResponseEntity.ok().build();
    }

    /**
     *
     * @param oauthServerType
     * @param code
     * @return
     */
    @GetMapping("/login/{oauthServerType}")
    ResponseEntity<Long> login(
            @PathVariable OauthServerType oauthServerType,
            @RequestParam("code") String code
    ) {
        Long login = oauthService.login(oauthServerType, code);

        return ResponseEntity.ok(login);
    }
}
