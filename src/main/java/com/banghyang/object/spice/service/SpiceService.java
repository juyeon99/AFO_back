package com.banghyang.object.spice.service;

import com.banghyang.object.line.entity.Line;
import com.banghyang.object.line.repository.LineRepository;
import com.banghyang.object.spice.dto.SpiceModifyRequest;
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

    /**
     * @return 모든 향료 response
     */
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

    /**
     * 향료 정보 수정 메소드
     * @param spiceModifyRequest
     */
    public void modifySpice(SpiceModifyRequest spiceModifyRequest) {
        // 수정할 향료 엔티티 가져오기
        Spice targetSpiceEntity = spiceRepository.findById(spiceModifyRequest.getId())
                .orElseThrow(() -> new IllegalArgumentException("항료 정보를 찾을 수 없습니다."));

        // 수정할 향료 엔티티를 복사하면서 수정
        Spice modifySpice = targetSpiceEntity.toBuilder()
                .name(targetSpiceEntity.getName())
                .nameKr(targetSpiceEntity.getNameKr())
                .description(targetSpiceEntity.getDescription())
                .build();

        // 수정한 향료 저장
        spiceRepository.save(modifySpice);

        // 이미지 엔티티 수정
        SpiceImage spiceImageEntity = spiceImageRepository.findBySpiceId(spiceModifyRequest.getId());
        // 기존 이미지 엔티티 있으면 수정
        if (spiceImageEntity != null) {
            SpiceImage modifySpiceImage = spiceImageEntity.toBuilder()
                    .url(spiceModifyRequest.getImageUrl())
                    .build();
            spiceImageRepository.save(modifySpiceImage);
        } else { // 없으면 생성
            SpiceImage newSpiceImage = SpiceImage.builder()
                    .url(spiceModifyRequest.getImageUrl())
                    .spice(modifySpice)
                    .build();
            spiceImageRepository.save(newSpiceImage);
        }
    }

    public void deleteSpice(Long spiceId) {
        spiceRepository.deleteById(spiceId);
    }
}
