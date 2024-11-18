package com.banghyang.auth.infra.oauth.kakao.dto;

import com.banghyang.auth.domain.OauthId;
import com.banghyang.auth.domain.OauthMember;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

import static com.banghyang.auth.domain.OauthServerType.KAKAO;

@JsonNaming(SnakeCaseStrategy.class)
public record KakaoMemberResponse(
        Long id,
        boolean hasSignedUp,
        LocalDateTime connectedAt,
        KakaoAccount kakaoAccount
) {
    public OauthMember toDomain() {
        return OauthMember.builder()
                .oauthId(new OauthId(String.valueOf(id), KAKAO))
                .name(kakaoAccount.name)
                .email(kakaoAccount.email)
                .birthyear(kakaoAccount.birthyear)
                .gender(kakaoAccount.gender)
                .build();
    }

    // 필요한 정보 가져오기
    @JsonNaming(SnakeCaseStrategy.class)
    public record KakaoAccount(
            boolean nameNeedsAgreement,
            String name, // 이름
            boolean emailNeedsAgreement,
            boolean isEmailValid, // 이메일 유효 여부
            boolean isEmailVerified, // 이메일 인증 여부
            String email, // 이메일
            boolean birthyearNeedsAgreement,
            String birthyear, // 출생연도
            boolean genderNeedsAgreement,
            String gender // 성별
    ) {
    }
}
