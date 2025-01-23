package com.banghyang.object.spice.service;

import com.banghyang.common.mapper.Mapper;
import com.banghyang.common.util.ValidUtils;
import com.banghyang.object.line.entity.Line;
import com.banghyang.object.line.repository.LineRepository;
import com.banghyang.object.spice.dto.SpiceCreateRequest;
import com.banghyang.object.spice.dto.SpiceModifyRequest;
import com.banghyang.object.spice.dto.SpiceResponse;
import com.banghyang.object.spice.entity.Spice;
import com.banghyang.object.spice.entity.SpiceImage;
import com.banghyang.object.spice.repository.SpiceImageRepository;
import com.banghyang.object.spice.repository.SpiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
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
        if (ValidUtils.isNotBlank(spiceCreateRequest.getLineName())) {
            // 계열 이름이 존재할 시에만 생성 진행
            // 계열이름으로 계열 엔티티 찾아오기
            Line lineEntity = lineRepository.findByName(spiceCreateRequest.getLineName());

            if (lineEntity != null) {
                // 계열 엔티티를 잘 찾아왔으면 생성 진행
                // 매퍼를 이용하여 리퀘스트의 정보로 엔티티 변환하여 생성
                Spice newSpiceEntity = Mapper.mapSpiceCreateRequestToEntity(spiceCreateRequest, lineEntity);
                spiceRepository.save(newSpiceEntity);

                // 향료 이미지
                if (ValidUtils.isNotBlank(spiceCreateRequest.getImageUrl())) {
                    // 만약 request 에 이미지 url 이 담겨있다면 이미지 엔티티 생성 진행
                    SpiceImage newSpiceImageEntity = SpiceImage.builder()
                            .spice(newSpiceEntity)
                            .url(spiceCreateRequest.getImageUrl())
                            .build();
                    spiceImageRepository.save(newSpiceImageEntity);
                }
            } else {
                // 계열 엔티티 찾아오지 못했을 시 예외 발생시키기
                throw new IllegalArgumentException("입력하신 계열명에 해당하는 계열 정보를 찾을 수 없습니다.");
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
        // 입력된 계열 이름으로 계열 엔티티 찾아오기
        Line lineEntity = lineRepository.findByName(spiceModifyRequest.getLineName());
        if (lineEntity != null) {
            // 향료 엔티티 수정
            Spice modifySpiceEntity = Spice.builder()
//                    .name(spiceModifyRequest.getName())
                    .nameKr(spiceModifyRequest.getNameKr())
//                    .description(spiceModifyRequest.getContent())
                    .line(lineEntity)
                    .build();
            // 향료 엔티티 클래스에 만들어둔 수정 메소드로 수정 진행
            targetSpiceEntity.modify(modifySpiceEntity);
        } else {
            throw new IllegalArgumentException("입력하신 계열명에 해당하는 계열 정보를 찾을 수 없습니다.");
        }

        // 향료 이미지
        if (ValidUtils.isNotBlank(spiceModifyRequest.getImageUrl())) {
            // request 에 이미지 url 정보가 담겨있으면 이미지 수정 진행
//            SpiceImage targetSpiceImageEntity = targetSpiceEntity.getSpiceImage();
            // 수정 엔티티 생성
            SpiceImage modifySpiceImageEntity = SpiceImage.builder()
                    .url(spiceModifyRequest.getImageUrl())
                    .spice(targetSpiceEntity)
                    .build();

//            if (targetSpiceImageEntity != null) {
//                // 기존 이미지 엔티티가 존재하면 수정
//                targetSpiceImageEntity.modify(modifySpiceImageEntity);
//            } else {
//                // 기존 이미지 엔티티가 없으면 생성
//                spiceImageRepository.save(modifySpiceImageEntity);
//            }
//        } else {
//            // request 에 이미지 url 입력정보가 없을 시 기존 이미지 존재 유무 확인 후 삭제
//            if (targetSpiceEntity.getSpiceImage() != null) {
//                spiceImageRepository.delete(targetSpiceEntity.getSpiceImage());
//            }
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
