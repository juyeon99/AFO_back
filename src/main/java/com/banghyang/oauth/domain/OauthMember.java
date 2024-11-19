package com.banghyang.oauth.domain;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "oauth_member",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "oauth_id_unique",
                        columnNames = {
                                "oauth_server_id",
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
    private OauthId oauthId;

    @Enumerated(EnumType.STRING)
    private OauthMemberRoleType role;

    private String name;
    private String email;
    private String birthyear;
    private String gender;

    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = OauthMemberRoleType.USER;
        }
    }
}
