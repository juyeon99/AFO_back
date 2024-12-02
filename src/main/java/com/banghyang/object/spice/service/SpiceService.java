package com.banghyang.object.spice.service;

import com.banghyang.object.line.entity.Line;
import com.banghyang.object.line.repository.LineRepository;
import com.banghyang.object.mapper.Mapper;
import com.banghyang.object.spice.dto.SpiceCreateRequest;
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

@Service
@RequiredArgsConstructor
public class SpiceService {

    private final SpiceRepository spiceRepository;
    private final SpiceImageRepository spiceImageRepository;
    private final LineRepository lineRepository;

    /**
     * @return 모든 향료 response 리스트 (영문명 오름차순 정렬)
     */
    public List<SpiceResponse> getAllSpiceResponse() {
        // 향료 모든 entity 가져와서 리스트에 담기
        List<Spice> spiceEntityList = spiceRepository.findAll();

        return spiceEntityList.stream() // stream 으로 엔티티 리스트의 모든 항목에 접근하여
                .map(Mapper::mapSpiceEntityToResponse) // 매퍼를 이용하여 entity 를 response 로 변환하고
                .sorted(Comparator.comparing(SpiceResponse::getName)) // 영문명 기준으로 정렬하여
                .toList(); // 리스트에 담아 반환하기
    }

    /**
     * 새로운 향로 생성 메소드(향료, 향료이미지)
     */
    public void createSpice(SpiceCreateRequest spiceCreateRequest) {
        if (spiceCreateRequest.getLineName() != null) {
            // 계열 이름이 존재할 시에만 생성 진행
            // 계열이름으로 계열 엔티티 찾아오기
            Line lineEntity = lineRepository.findByName(spiceCreateRequest.getLineName());
            // 매퍼를 이용하여 리퀘스트의 정보로 엔티티 변환하여 생성
            Spice newSpiceEntity = Mapper.mapSpiceCreateRequestToEntity(spiceCreateRequest, lineEntity);
            // 생성한 향료 엔티티 저장
            spiceRepository.save(newSpiceEntity);

            // 향료 이미지
            if (spiceCreateRequest.getImageUrl() != null) {
                // 만약 request 에 이미지 url 이 담겨있다면 이미지 엔티티 생성 진행
                SpiceImage newSpiceImageEntity = SpiceImage.builder()
                        .spice(newSpiceEntity)
                        .url(spiceCreateRequest.getImageUrl())
                        .build();
                spiceImageRepository.save(newSpiceImageEntity);
            }
        } else {
            // 계열 정보가 존재하지 않는다면 예외 발생시키기
            throw new IllegalArgumentException("향료 등록에 필요한 계열 정보가 존재하지 않습니다.");
        }
    }

    /**
     * 향료 정보 수정 메소드
     */
    public void modifySpice(SpiceModifyRequest spiceModifyRequest) {
        // 수정할 향료 엔티티 찾아오기
        Spice targetSpiceEntity = spiceRepository.findById(spiceModifyRequest.getId())
                .orElseThrow(() -> new IllegalArgumentException("수정하려는 향료의 정보를 찾을 수 없습니다."));
        // toBuilder 사용하여 향료 엔티티 수정
        Spice modifySpiceEntity = targetSpiceEntity.toBuilder()
                .name(spiceModifyRequest.getName())
                .nameKr(spiceModifyRequest.getNameKr())
                .description(spiceModifyRequest.getDescription())
                .line(lineRepository.findByName(spiceModifyRequest.getLineName()))
                .build();
        // 수정한 향료 엔티티 저장
        spiceRepository.save(modifySpiceEntity);

        // 향료 이미지
        if (spiceModifyRequest.getImageUrl() != null) {
            // request 에 이미지 url 정보가 담겨있으면 이미지 수정 진행
            SpiceImage targetSpiceImageEntity = spiceImageRepository.findBySpiceId(modifySpiceEntity.getId());
            SpiceImage modifySpiceImageEntity = targetSpiceImageEntity.toBuilder()
                    .url(spiceModifyRequest.getImageUrl())
                    .spice(modifySpiceEntity)
                    .build();
            spiceImageRepository.save(modifySpiceImageEntity);
        }
    }

    /**
     * 향료 삭제 메소드
     */
    public void deleteSpice(Long spiceId) {
        Spice targetSpiceEntity = spiceRepository.findById(spiceId)
                .orElseThrow(() -> new IllegalArgumentException("삭제하려는 향료의 정보를 찾을 수 없습니다."));
        spiceRepository.delete(targetSpiceEntity);
    }
}
