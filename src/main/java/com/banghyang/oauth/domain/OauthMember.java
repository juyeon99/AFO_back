package com.banghyang.oauth.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "oauth_member", // 테이블명
        uniqueConstraints = { // 중복 불가 항복들
                @UniqueConstraint(
                        name = "oauth_id_unique",
                        columnNames = {
                                "oauth_server_id", // oauthServerId 중복 불가 설정으로 유일함 확보
                                "oauth_server"
                        }
                ),
        }
)
public class OauthMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private OauthId oauthId;            // OauthServerId

    @Enumerated(EnumType.STRING)
    private OauthMemberRoleType role;   // 권한

    private String name;                // 사용자명
    private String email;               // 이메일
    private String birthyear;           // 촐생연도
    private String gender;              // 성별
    private LocalDateTime createdAt;    // 가입일시
    private LocalDateTime deletedAt;    // 탈퇴일시

    /**
     * 권한, 가입일시 초기값 설정 메소드
     */
    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = OauthMemberRoleType.USER;
        }

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    /**
     * 회원정보 조회에 필요한 항목들만 포함한 생성자 메소드
     * @param name
     * @param email
     * @param birthyear
     * @param gender
     */
    @Builder(toBuilder = true)
    public OauthMember(String name, String email, String birthyear, String gender) {
        this.name = name;
        this.email = email;
        this.birthyear = birthyear;
        this.gender = gender;
    }
}
