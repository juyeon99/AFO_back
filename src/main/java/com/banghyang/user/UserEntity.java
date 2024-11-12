package com.banghyang.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 사용자 아이디
    private String email;       // 사용자 이메일
    private String name;        // 사용자명
    private String gender;      // 사용자 성별
    private LocalDate birthday; // 사용자 생년월일
    private LocalDateTime time; // 사용자 가입일시

    @Enumerated(value = EnumType.STRING)
    private Role role;          // 사용자 권한 Enum

    // 가입일시 자동 입력 메소드
    @PrePersist
    protected void onCreate() {
        this.time = LocalDateTime.now();
    }

    // 빌더 패턴 생성자 사용
    @Builder(toBuilder = true)
    public UserEntity(String email, String name, String gender, LocalDate birthday, LocalDateTime time) {
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.time = time;
        this.role = Role.valueOf("USER");
    }
}
