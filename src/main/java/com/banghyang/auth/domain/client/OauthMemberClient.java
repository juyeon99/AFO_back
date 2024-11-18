package com.banghyang.auth.domain.client;

import com.banghyang.auth.domain.OauthMember;
import com.banghyang.auth.domain.OauthServerType;

public interface OauthMemberClient {
    OauthServerType supportServer();
    OauthMember fetch(String code);
}
