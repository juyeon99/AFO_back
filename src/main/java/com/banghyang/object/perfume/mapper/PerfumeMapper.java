package com.banghyang.object.perfume.mapper;

import com.banghyang.object.perfume.dto.PerfumeResponse;
import com.banghyang.object.perfume.entity.Perfume;
import com.banghyang.object.perfume.entity.PerfumeImage;

import java.util.Collections;
import java.util.Comparator;

public class PerfumeMapper {
    public static PerfumeResponse mapPerfumeToResponse(Perfume perfumeEntity) {
        PerfumeResponse perfumeResponse = new PerfumeResponse(); // 내용 담을 response 생성
        perfumeResponse.setId(perfumeEntity.getId()); // 향수 id 담기
        perfumeResponse.setName(perfumeEntity.getName()); // 향수 이름 담기
        perfumeResponse.setDescription(perfumeEntity.getDescription()); // 향수 설명 담기

        if (perfumeEntity.getImageList() != null) {
            // 엔티티의 이미지 리스트가 존재할 시에
            perfumeResponse.setImageUrlList( // response 에 아래의 과정으로 담아줌
                    perfumeEntity.getImageList().stream() // stream 으로 모든 항목에 접근하여
                            .sorted(Comparator.comparing(PerfumeImage::getId)) // id 기준으로 정렬시키고
                            .map(PerfumeImage::getUrl) // url 을 가져와서
                            .toList() // 리스트로 담아줌
            );
        } else {
            // 이미지 리스트가 없다면
            perfumeResponse.setImageUrlList(Collections.emptyList()); // 빈 리스트 담기
        }

        // 싱글노트 존재시에 담고, 없을시 null
        perfumeResponse.setSingleNote(perfumeEntity.getSingleNote() != null ?
                perfumeEntity.getSingleNote().getSpices() : null);

        // 탑노트
        perfumeResponse.setTopNote(perfumeResponse.getTopNote() != null ?
                perfumeEntity.getTopNote().getSpices() : null);

        // 미들노트
        perfumeResponse.setMiddleNote(perfumeResponse.getMiddleNote() != null ?
                perfumeEntity.getMiddleNote().getSpices() : null);

        // 베이스노트
        perfumeResponse.setBaseNote(perfumeResponse.getBaseNote() != null ?
                perfumeEntity.getBaseNote().getSpices() : null);

        return perfumeResponse;
    }
}
