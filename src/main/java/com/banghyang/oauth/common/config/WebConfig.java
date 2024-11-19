package com.banghyang.oauth.common.config;

import com.banghyang.oauth.infra.presentation.OauthServerTypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.HttpMethod;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    /**
     * CORS 설정
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.PATCH.name()
                )
                .allowCredentials(true)
                .exposedHeaders("*");
    }

    /**
     * url 을 OauthServerType 으로 반환시키는 converter 호출
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new OauthServerTypeConverter());
    }
}
