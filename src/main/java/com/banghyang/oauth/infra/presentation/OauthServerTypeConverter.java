package com.banghyang.oauth.infra.presentation;

import com.banghyang.oauth.domain.OauthServerType;
import org.springframework.core.convert.converter.Converter;

public class OauthServerTypeConverter implements Converter<String, OauthServerType> {
    /**
     * String 을 OauthServerType 으로 변환
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return OauthServerType
     */
    @Override
    public OauthServerType convert(String source) {
        return OauthServerType.fromName(source);
    }
}
