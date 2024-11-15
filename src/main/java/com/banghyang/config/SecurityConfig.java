package com.banghyang.config;

import com.banghyang.member.domain.entity.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configurable
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 접근 권한 설정
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/oauth-login/admin").hasRole(MemberRole.ADMIN.name())
                .requestMatchers("/oauth-login/info").authenticated()
                .anyRequest().permitAll()
        );

        // 폼 로그인 설정
        http.formLogin((auth) -> auth
                .loginPage("/oauth-login/login")
                .loginProcessingUrl("/oauth-login/loginProc")
                .usernameParameter("loginId")
                .passwordParameter("password")
                .defaultSuccessUrl("/oauth-login")
                .failureUrl("/oauth-login")
                .permitAll()
        );

        // OAuth2 로그인 설정
        http.oauth2Login((auth) -> auth
                .loginPage("oauth-login/login")
                .defaultSuccessUrl("/oauth-login")
                .failureUrl("/oauth-login/login")
                .permitAll()
        );

        // 로그아웃 URL 설정
        http.logout((auth) -> auth
                .logoutUrl("/logout")
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
