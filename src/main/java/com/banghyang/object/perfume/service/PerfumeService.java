package com.banghyang.object.perfume.service;

import com.banghyang.object.mapper.Mapper;
import com.banghyang.object.note.entity.BaseNote;
import com.banghyang.object.note.entity.MiddleNote;
import com.banghyang.object.note.entity.SingleNote;
import com.banghyang.object.note.entity.TopNote;
import com.banghyang.object.note.repository.BaseNoteRepository;
import com.banghyang.object.note.repository.MiddleNoteRepository;
import com.banghyang.object.note.repository.SingleNoteRepository;
import com.banghyang.object.note.repository.TopNoteRepository;
import com.banghyang.object.perfume.dto.PerfumeCreateRequest;
import com.banghyang.object.perfume.dto.PerfumeModifyRequest;
import com.banghyang.object.perfume.dto.PerfumeResponse;
import com.banghyang.object.perfume.entity.Perfume;
import com.banghyang.object.perfume.entity.PerfumeImage;
import com.banghyang.object.perfume.repository.PerfumeImageRepository;
import com.banghyang.object.perfume.repository.PerfumeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PerfumeService {

    private final PerfumeRepository perfumeRepository;
    private final PerfumeImageRepository perfumeImageRepository;
    private final SingleNoteRepository singleNoteRepository;
    private final TopNoteRepository topNoteRepository;
    private final MiddleNoteRepository middleNoteRepository;
    private final BaseNoteRepository baseNoteRepository;

    /**
     * @return 모든 향수 response 리스트(name 기준 오름차순 정렬)
     */
    public List<PerfumeResponse> getAllPerfumeResponses() {
        // perfume 엔티티 전체 가져와서 리스트에 담기
        List<Perfume> perfumeEntityList = perfumeRepository.findAll();

        return perfumeEntityList.stream() // 엔티티 리스트의 모든 항목에 stream 으로 접근
                .map(Mapper::mapPerfumeEntityToResponse) // mapper 메소드를 이용하여 response 로 변환
                .sorted(Comparator.comparing(PerfumeResponse::getName, String.CASE_INSENSITIVE_ORDER)) // 이름순 정렬하여
                .toList(); // 리스트에 담아서 반환
    }

    /**
     * 새로운 향수 정보 생성 메소드(향수, 향수이미지, 노트)
     */
    public void createPerfume(PerfumeCreateRequest perfumeCreateRequest) {
        // 새로운 향수 정보 담을 perfume 엔티티 생성
        // Mapper 클래스의 request -> entity 변환 메소드로 정보 담아줌
        Perfume newPerfumeEntity = Mapper.mapPerfumeCreateRequestToEntity(perfumeCreateRequest);
        // 정보 담은 엔티티 저장
        perfumeRepository.save(newPerfumeEntity);

        // 향수 이미지
        if (perfumeCreateRequest.getImageUrl() != null) {
            // 만약 request 에 이미지 url 정보가 담겨있다면 이미지 엔티티 생성 진행
            PerfumeImage newPerfumeImageEntity = PerfumeImage.builder()
                    .perfume(newPerfumeEntity)
                    .url(perfumeCreateRequest.getImageUrl())
                    .build();
            perfumeImageRepository.save(newPerfumeImageEntity);
        }

        // 노트
        if (perfumeCreateRequest.getSingleNote() != null) { // 싱글노트가 존재하고,
            if (perfumeCreateRequest.getTopNote() == null &&
                    perfumeCreateRequest.getMiddleNote() == null &&
                    perfumeCreateRequest.getBaseNote() == null) { // 나머지 노트들이 다 null 일때
                SingleNote newSingleNoteEntity = SingleNote.builder()
                        .perfume(newPerfumeEntity)
                        .spices(perfumeCreateRequest.getSingleNote())
                        .build();
                singleNoteRepository.save(newSingleNoteEntity); // 싱글노트 엔티티 생성하고 향수 엔티티와 연결
            } else {
                // 만약 싱글 노트와 다른 노트가 동시에 존재하면 예외 발생시키기
                throw new IllegalArgumentException("싱글 노트와 다른 종류의 노트가 동시에 존재할 수 없습니다.");
            }
        } else { // 싱글노트가 존재하지 않는 경우 탑, 미들, 베이스 노트 생성 진행
            // 탑노트
            if (perfumeCreateRequest.getTopNote() != null) {
                TopNote newTopNoteEntity = TopNote.builder()
                        .perfume(newPerfumeEntity)
                        .spices(perfumeCreateRequest.getTopNote())
                        .build();
                topNoteRepository.save(newTopNoteEntity);
            }

            // 미들노트
            if (perfumeCreateRequest.getMiddleNote() != null) {
                MiddleNote newMiddleNoteEntity = MiddleNote.builder()
                        .perfume(newPerfumeEntity)
                        .spices(perfumeCreateRequest.getMiddleNote())
                        .build();
                middleNoteRepository.save(newMiddleNoteEntity);
            }

            // 베이스노트
            if (perfumeCreateRequest.getBaseNote() != null) {
                BaseNote newBaseNoteEntity = BaseNote.builder()
                        .perfume(newPerfumeEntity)
                        .spices(perfumeCreateRequest.getBaseNote())
                        .build();
                baseNoteRepository.save(newBaseNoteEntity);
            }

            if (perfumeCreateRequest.getTopNote() == null &&
                    perfumeCreateRequest.getMiddleNote() == null &&
                    perfumeCreateRequest.getBaseNote() == null) {
                // 노트가 아무것도 존재하지 않을 시 예외 발생시키기
                throw new IllegalArgumentException("노트 정보가 존재하지 않아 향수 등록을 실패했습니다.");
            }
        }
    }

    /**
     * 향수 정보 수정 메소드
     */
    public void modifyPerfume(PerfumeModifyRequest perfumeModifyRequest) {
        // 수정할 perfume 엔티티 request 의 id 값으로 찾아오기
        Perfume targetPerfumeEntity = perfumeRepository.findById(perfumeModifyRequest.getId())
                .orElseThrow(() -> new IllegalArgumentException("수정하려 하는 향수의 정보를 찾을 수 없습니다."));
        // toBuilder 사용하여 찾아온 perfume 엔티티 수정
        Perfume modifyPerfumeEntity = Perfume.builder()
                .name(perfumeModifyRequest.getName())
                .description(perfumeModifyRequest.getDescription())
                .brand(perfumeModifyRequest.getBrand())
                .grade(perfumeModifyRequest.getGrade())
                .build();
        // 향수 엔티티 클래스에 만들어둔 수정 메소드로 수정 적용
        targetPerfumeEntity.modify(modifyPerfumeEntity);
        System.out.println(modifyPerfumeEntity); // 수정된 향수 엔티티 확인

        // 향수 이미지
        if (perfumeModifyRequest.getImageUrl() != null) {
            // request 에 이미지 URL 존재할 시 이미지 수정 진행
            PerfumeImage targetPerfumeImageEntity = targetPerfumeEntity.getPerfumeImage();
            if (targetPerfumeImageEntity != null) {
                PerfumeImage modifyPerfumeImageEntity = PerfumeImage.builder()
                        .url(perfumeModifyRequest.getImageUrl())
                        .perfume(modifyPerfumeEntity)
                        .build();
                targetPerfumeImageEntity.modify(modifyPerfumeImageEntity);
            } else {
                throw new IllegalArgumentException("수정하려는 향수 이미지 정보를 찾을 수 없습니다.");
            }
        }

        // 노트
        if (perfumeModifyRequest.getSingleNote() != null) {
            if (perfumeModifyRequest.getTopNote() == null &&
                    perfumeModifyRequest.getMiddleNote() == null &&
                    perfumeModifyRequest.getBaseNote() == null) {
                // request 에 싱글노트가 존재하고 다른 노트가 없을 시 싱글노트 수정 진행
                SingleNote modifySingleNoteEntity = SingleNote.builder()
                        .perfume(modifyPerfumeEntity)
                        .spices(perfumeModifyRequest.getSingleNote())
                        .build();
                targetPerfumeEntity.getSingleNote().modify(modifySingleNoteEntity);

                // 싱글노트 외의 다른 노트 엔티티 모두 삭제
                // 탑노트 삭제
                if (targetPerfumeEntity.getTopNote() != null) {
                    topNoteRepository.delete(targetPerfumeEntity.getTopNote());
                }
                // 미들노트 삭제
                if (targetPerfumeEntity.getMiddleNote() != null) {
                    middleNoteRepository.delete(targetPerfumeEntity.getMiddleNote());
                }
                // 베이스노트 삭제
                if (targetPerfumeEntity.getBaseNote() != null) {
                    baseNoteRepository.delete(targetPerfumeEntity.getBaseNote());
                }
            } else {
                // request 에 싱글노트와 다른 노트가 함께 존재시 예외 발생시키기
                throw new IllegalArgumentException("싱글 노트와 다른 종류의 노트가 동시에 존재할 수 없습니다.");
            }
        } else { // 싱글노트가 존재하지 않을 시 싱글 노트 엔티티 삭제하고 탑, 미들, 베이스 노트 수정 진행
            // 탑노트
            if (perfumeModifyRequest.getTopNote() != null) {
                // 싱글노트 존재 시 삭제하기
                if (targetPerfumeEntity.getSingleNote() != null) {
                    singleNoteRepository.delete(targetPerfumeEntity.getSingleNote());
                }

                TopNote modifyTopNoteEntity = TopNote.builder()
                        .perfume(modifyPerfumeEntity)
                        .spices(perfumeModifyRequest.getTopNote())
                        .build();
                targetPerfumeEntity.getTopNote().modify(modifyTopNoteEntity);
            }
            // 미들노트
            if (perfumeModifyRequest.getMiddleNote() != null) {
                // 싱글노트 존재 시 삭제하기
                if (targetPerfumeEntity.getSingleNote() != null) {
                    singleNoteRepository.delete(targetPerfumeEntity.getSingleNote());
                }

                MiddleNote modifyMiddleNoteEntity = MiddleNote.builder()
                        .perfume(modifyPerfumeEntity)
                        .spices(perfumeModifyRequest.getMiddleNote())
                        .build();
                targetPerfumeEntity.getMiddleNote().modify(modifyMiddleNoteEntity);
            }
            // 베이스노트
            if (perfumeModifyRequest.getBaseNote() != null) {
                // 싱글노트 존재 시 삭제하기
                if (targetPerfumeEntity.getSingleNote() != null) {
                    singleNoteRepository.delete(targetPerfumeEntity.getSingleNote());
                }

                BaseNote modifyBaseNoteEntity = BaseNote.builder()
                        .perfume(modifyPerfumeEntity)
                        .spices(perfumeModifyRequest.getBaseNote())
                        .build();
                targetPerfumeEntity.getBaseNote().modify(modifyBaseNoteEntity);
            } // 노트 정보가 존재하지 않으면 노트 수정은 진행하지 않음
        }
    }

    /**
     * 향수 삭제 메소드
     */
    public void deletePerfume(Long perfumeId) {
        Perfume targetPerfumeEntity = perfumeRepository.findById(perfumeId)
                .orElseThrow(() -> new IllegalArgumentException("삭제하려는 향수의 정보를 찾을 수 업습니다."));
        perfumeRepository.delete(targetPerfumeEntity);
    }
}
