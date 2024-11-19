package com.banghyang.oauth.infra.oauth.config;

import com.banghyang.oauth.infra.oauth.kakao.client.KakaoApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpInterfaceConfig {
    /**
     * Http Interface Client 구현체를 빈으로 등록
     * @return
     */
    @Bean
    public KakaoApiClient kakaoApiClient() {
        return createHttpInterface(KakaoApiClient.class);
    }

    /**
     * 인터페이스를 구현하는 프록시 객체
     * @param clazz
     * @return clazz 로 전달된 인터페이스 타입의 HTTP 클라이언트 반환
     * @param <T> 생성할 클라이언트 타입
     */
    private <T> T createHttpInterface(Class<T> clazz) {
        WebClient webClient = WebClient.create(); // WebClient : WebFlux 에서 비동기 HTTP 요청을 보내기 위해 사용
        HttpServiceProxyFactory build = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(webClient)).build();
        return build.createClient(clazz);
    }
}
