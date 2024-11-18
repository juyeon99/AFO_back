package com.banghyang.auth.kakao.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoTokenResponseDto {

    @JsonProperty("token_type")
    private String tokenType; // 토큰 타입 bearer 고정

    @JsonProperty("access_token")
    private String accessToken; // 액세스 토큰

    @JsonProperty("expires_in")
    private Integer expiresIn; // 액세스 토큰 만료시간(초)

    @JsonProperty("refresh_token")
    private String refreshToken; // 리프레시 토큰

    @JsonProperty("refresh_token_expires_in")
    private Integer refreshTokenExpiresIn; // 리프레시 토큰 만료시간(초)

    @JsonProperty("id_token")
    private String idToken;

    @JsonProperty("scope")
    private String scope;
}
