package com.banghyang.member.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // 회원 식별자
    private String loginId;             // 로그인에 사용할 아이디
    private String password;            // 회원 비밀번호
    private String name;                // 회원명
    private String gender;              // 회원 성별
    private LocalDate birthday;         // 회원 생년월일
    private LocalDateTime createAt;     // 회원 가입일시
    private String provider;            // 연동 로그인시 사용하는 provider
    private String providerId;          // 연동 로그인 한 유저의 고유식별자

    @Enumerated(value = EnumType.STRING)
    private MemberRole role; // 회원 권한 Enum

    // 가입일시 자동 입력 메소드
    @PrePersist
    private void onCreate() {
        this.createAt = LocalDateTime.now();
    }

    // 빌더 패턴 생성자 사용
    @Builder(toBuilder = true)
    public Member(String loginId, String password, String name, String gender, LocalDate birthday, String provider, String providerId, MemberRole role) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.provider = provider;
        this.providerId = providerId;
        this.role = MemberRole.USER;
    }
}
