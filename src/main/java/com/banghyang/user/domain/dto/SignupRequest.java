package com.banghyang.user.domain.dto;

import com.banghyang.user.domain.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class SignupRequest {

    private String email;       // 사용자 이메일
    private String password;    // 사용자 비밀번호
    private String name;        // 사용자명
    private String gender;      // 사용자 성별
    private LocalDate birthday; // 사용자 생년월일

    public User toEntity() {
        return User.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .gender(this.gender)
                .birthday(this.birthday)
                .build();
    }

}
