package com.banghyang.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 사용자 아이디
    private String email;       // 사용자 이메일
    private String password;    // 사용자 비밀번호
    private String name;        // 사용자명
    private String gender;      // 사용자 성별
    private LocalDate birthday; // 사용자 생년월일
    private LocalDateTime time; // 사용자 가입일시
    private String provider;    // provider : google
    private String providerId;  // 구글 로그인 한 유저의 고유 ID

    @Enumerated(value = EnumType.STRING)
    private Role role;          // 사용자 권한 Enum

    // 가입일시 자동 입력 메소드
    @PrePersist
    protected void onCreate() {
        this.time = LocalDateTime.now();
    }

    // 빌더 패턴 생성자 사용
    @Builder(toBuilder = true)

    public User(String email, String password, String name, String gender, LocalDate birthday) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.role = Role.USER;
    }
}
