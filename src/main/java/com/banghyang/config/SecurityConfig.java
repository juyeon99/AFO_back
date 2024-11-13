package com.banghyang.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configurable
@EnableWebSecurity
public class SecurityConfig {

    // 시큐리티 필터 메소드
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "users/login", "/users/signup").permitAll() // 모두에게 허용할 URL
                .anyRequest().authenticated()
        );

        // 로그인 설정
        http.formLogin((auth) -> auth
                .loginPage("/users/login") // 로그인 페이지
                .loginProcessingUrl("/users/loginProc") // 프론트에서 넘어온 정보 넘길 URL(Spring Security가 자동으로 로그인 진행)
                .permitAll()
        );

        // 로그아웃 URL 설정
        http.logout((auth) -> auth
                .logoutUrl("/users/logout")
        );

        // csrf : 사이트 위변조 방지 설정(스프링 시큐리티에는 자동으로 설정 되어 있음)
        // csrf 기능이 켜져있으면 post 요청을 보낼때 csrf 토큰도 보내줘야 로그인 진행됨
        // 개발단계에서만 csrf 잠시 꺼두기
        http.csrf((auth) -> auth.disable());

        return http.build();
    }

    // BCrypt password encoder 리턴하는 메소드
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
