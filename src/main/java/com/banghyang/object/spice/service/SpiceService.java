package com.banghyang.object.spice.service;

import com.banghyang.object.line.entity.Line;
import com.banghyang.object.line.repository.LineRepository;
import com.banghyang.object.spice.dto.SpiceResponse;
import com.banghyang.object.spice.entity.Spice;
import com.banghyang.object.spice.entity.SpiceImage;
import com.banghyang.object.spice.repository.SpiceImageRepository;
import com.banghyang.object.spice.repository.SpiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SpiceService {

    private final SpiceRepository spiceRepository;
    private final SpiceImageRepository spiceImageRepository;
    private final LineRepository lineRepository;

    public List<SpiceResponse> getAllSpiceResponses() {

        // 모든 향료 엔티티 찾아오기
        List<Spice> allSpiceEntityList = spiceRepository.findAll();

        // Response List 만들기
        List<SpiceResponse> allSpiceResponseList = allSpiceEntityList.stream().map(spice -> {
                    // 향료 아이디로 이미지 엔티티 찾아오기
                    SpiceImage spiceImageEntity = spiceImageRepository.findBySpiceId(spice.getId());
                    // 계열 엔티티
                    Line lineEntity = spice.getLine();

                    // 이미지와 계열 엔티티가 모두 존재할 시 response 반환
                    if (spiceImageEntity != null && lineEntity != null) {
                        return new SpiceResponse().from(spice, spiceImageEntity, lineEntity);
                    }

                    // 하나라도 없으면 null 반환
                    return null;

                    // 필터로 null 값은 제외하고 리스트로 변환
                }).filter(Objects::nonNull)
                // 이름 기준 정렬
                .sorted(Comparator.comparing(SpiceResponse::getName))
                .toList();

        return allSpiceResponseList;
    }
}
