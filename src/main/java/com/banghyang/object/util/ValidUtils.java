package com.banghyang.object.util;

import com.banghyang.chat.dto.PerfumeRecommendResponse;

public class ValidUtils {

    public static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static void validPerfumeRecommendResponseFields(PerfumeRecommendResponse perfumeRecommendResponse) {
        if (perfumeRecommendResponse.getMode() == null ||
                perfumeRecommendResponse.getLineId() == null ||
                !ValidUtils.isNotBlank(perfumeRecommendResponse.getContent()) ||
                perfumeRecommendResponse.getRecommendations() == null) {
            throw new IllegalArgumentException("향수 추천 결과 생성 중 오류가 발생했습니다.");
        }
    }
}
