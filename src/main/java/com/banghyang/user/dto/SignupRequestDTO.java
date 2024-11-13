package com.banghyang.user.dto;

import com.banghyang.user.entity.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class SignupRequestDTO {

    private String email;       // 사용자 이메일
    private String name;        // 사용자명
    private String gender;      // 사용자 성별
    private LocalDate birthday; // 사용자 생년월일

    public UserEntity toEntity() {
        return UserEntity.builder()
                .email(this.email)
                .name(this.name)
                .gender(this.gender)
                .birthday(this.birthday)
                .build();
    }

}
