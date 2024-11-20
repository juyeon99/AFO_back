package com.banghyang.member.oauth.model.dto;

import com.banghyang.oauth.domain.OauthMember;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class OauthMemberDTO {
    private String name;
    private String email;
    private String birthyear;
    private String gender;

    @Builder(toBuilder = true)
    public OauthMemberDTO(String name, String email, String birthyear, String gender) {
        this.name = name;
        this.email = email;
        this.birthyear = birthyear;
        this.gender = gender;
    }

    /**
     * DTO -> Entity 객체 변환
     */
    public OauthMember dtoToEntity() {
        return OauthMember.builder()
                .name(this.name)
                .email(this.email)
                .birthyear(this.birthyear)
                .gender(this.gender)
                .build();
    }
}
