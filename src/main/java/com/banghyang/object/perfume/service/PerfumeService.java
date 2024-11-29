package com.banghyang.object.perfume.service;

import com.banghyang.object.note.entity.BaseNote;
import com.banghyang.object.note.entity.MiddleNote;
import com.banghyang.object.note.entity.SingleNote;
import com.banghyang.object.note.entity.TopNote;
import com.banghyang.object.note.repository.BaseNoteRepository;
import com.banghyang.object.note.repository.MiddleNoteRepository;
import com.banghyang.object.note.repository.SingleNoteRepository;
import com.banghyang.object.note.repository.TopNoteRepository;
import com.banghyang.object.perfume.dto.*;
import com.banghyang.object.perfume.entity.Perfume;
import com.banghyang.object.perfume.entity.PerfumeImage;
import com.banghyang.object.perfume.repository.PerfumeImageRepository;
import com.banghyang.object.perfume.repository.PerfumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerfumeService {

    private final PerfumeRepository perfumeRepository;
    private final PerfumeImageRepository perfumeImageRepository;
    private final SingleNoteRepository singleNoteRepository;
    private final TopNoteRepository topNoteRepository;
    private final MiddleNoteRepository middleNoteRepository;
    private final BaseNoteRepository baseNoteRepository;

    /**
     * @return 모든 향수 response (name 기준 오름차순 정렬)
     */
    public List<PerfumeResponse> getAllPerfumeResponses() {
        // 모든 perfume 엔티티 가져오기
        List<Perfume> allPerfumeEntityList = perfumeRepository.findAll();

        // stream 이용하여 엔티티 -> response 로 변환하며 담아 리스트 생성
        List<PerfumeResponse> allPerfumeResponseList = allPerfumeEntityList.stream().map(perfumeEntity -> {
            PerfumeResponse perfumeResponse = new PerfumeResponse(); // 내용 담을 response 생성
            perfumeResponse.setId(perfumeEntity.getId()); // 향수 id 담기
            perfumeResponse.setName(perfumeEntity.getName()); // 향수 이름 담기
            perfumeResponse.setDescription(perfumeEntity.getDescription()); // 향수 설명 담기

            // image 엔티티 조회
            PerfumeImage perfumeImageEntity = perfumeImageRepository.findByPerfumeId(perfumeEntity.getId());
            if (perfumeImageEntity != null) {
                // 향수 이미지가 존재하면 response 에 담기
                perfumeResponse.setImageUrl(perfumeImageEntity.getUrl());
            } else {
                // 향수 이미지가 존재하지 않으면 null 담기
                perfumeResponse.setImageUrl(null);
            }

            // singleNote 엔티티 조회
            SingleNote singleNoteEntity = singleNoteRepository.findByPerfumeId(perfumeEntity.getId());
            if (singleNoteEntity != null) {
                // 싱글노트가 존재하면 response 에 담기
                perfumeResponse.setSingleNote(singleNoteEntity.getSpices());
            } else {
                // 싱글노트가 존재하지 않으면 null 담기
                perfumeResponse.setSingleNote(null);
            }

            // topNote 엔티티 조회
            TopNote topNoteEntity = topNoteRepository.findByPerfumeId(perfumeEntity.getId());
            if (topNoteEntity != null) {
                perfumeResponse.setTopNote(topNoteEntity.getSpices());
            } else {
                perfumeResponse.setTopNote(null);
            }

            // middleNote 엔티티 조회
            MiddleNote middleNoteEntity = middleNoteRepository.findByPerfumeId(perfumeEntity.getId());
            if (middleNoteEntity != null) {
                // 미들노트가 존재하면 response 에 담기
                perfumeResponse.setMiddleNote(middleNoteEntity.getSpices());
            } else {
                perfumeResponse.setMiddleNote(null);
            }

            // baseNote 엔티티 조회
            BaseNote baseNoteEntity = baseNoteRepository.findByPerfumeId(perfumeEntity.getId());
            if (baseNoteEntity != null) {
                perfumeResponse.setBaseNote(baseNoteEntity.getSpices());
            } else {
                perfumeResponse.setBaseNote(null);
            }

            // 내용 담은 response 반환
            return perfumeResponse;

            // 이름 오름차순으로 정렬하여 리스트로 담기
        }).sorted(Comparator.comparing(PerfumeResponse::getName, String.CASE_INSENSITIVE_ORDER)).toList();


        return allPerfumeResponseList;
    }

    /**
     * 향수 정보 수정 메소드
     */
    public void modifyPerfume(PerfumeModifyRequest perfumeModifyRequest) {
        // 수정할 향수 엔티티 가져오기
        Perfume targetPerfumeEntity = perfumeRepository.findById(perfumeModifyRequest.getId())
                .orElseThrow(() -> new IllegalArgumentException("향수 정보를 찾을 수 없습니다."));

        // 수정할 향수 엔티티 복사 및 수정
        Perfume modifyPerfumeEntity = targetPerfumeEntity.toBuilder()
                .name(perfumeModifyRequest.getName())
                .description(perfumeModifyRequest.getDescription())
                .brand(perfumeModifyRequest.getBrand())
                .grade(perfumeModifyRequest.getGrade())
                .build();

        // 수정한 향수 저장
        perfumeRepository.save(modifyPerfumeEntity);

        // request 에 imageUrl 있으면 image url 수정 진행
        if (perfumeModifyRequest.getImageUrl() != null) {
            // 수정할 이미지 찾아오기
            PerfumeImage targetPerfumeImageEntity = perfumeImageRepository.findByPerfumeId(modifyPerfumeEntity.getId());
            if (targetPerfumeImageEntity != null) {
                // 해당하는 이미지가 있다면 수정 진행
                PerfumeImage modifyPerfumeImageEntity = targetPerfumeImageEntity.toBuilder()
                        .url(perfumeModifyRequest.getImageUrl())
                        .build();
                perfumeImageRepository.save(modifyPerfumeImageEntity);
            } else {
                // 해당 이미지 없으면 새로 생성
                PerfumeImage newPerfumeImageEntity = PerfumeImage.builder()
                        .url(perfumeModifyRequest.getImageUrl())
                        .perfume(modifyPerfumeEntity)
                        .build();
                perfumeImageRepository.save(newPerfumeImageEntity);
            }
        }

        // request 에 singleNote 있으면 singleNote 수정 진행
        if (perfumeModifyRequest.getSingleNote() != null) {
            // 수정할 싱글노트 엔티티 찾아오기
            SingleNote targetSingleNoteEntity = singleNoteRepository.findByPerfumeId(modifyPerfumeEntity.getId());
            if (targetSingleNoteEntity != null) {
                // 수정할 싱글노트 존재시 수정 진행
                SingleNote modifySingleNoteEntity = targetSingleNoteEntity.toBuilder()
                        .spices(perfumeModifyRequest.getSingleNote())
                        .build();
                singleNoteRepository.save(modifySingleNoteEntity);
            } else {
                // 없을시 생성 진행
                SingleNote newSingleNoteEntity = SingleNote.builder()
                        .spices(perfumeModifyRequest.getSingleNote())
                        .perfume(modifyPerfumeEntity)
                        .build();
                singleNoteRepository.save(newSingleNoteEntity);
            }
        }

        // TopNote 수정
        if (perfumeModifyRequest.getTopNote() != null) {
            TopNote targetTopNoteEntity = topNoteRepository.findByPerfumeId(modifyPerfumeEntity.getId());
            if (targetTopNoteEntity != null) {
                TopNote modifuTopNoteEntity = targetTopNoteEntity.toBuilder()
                        .spices(perfumeModifyRequest.getTopNote())
                        .build();
                topNoteRepository.save(modifuTopNoteEntity);
            } else {
                TopNote newTopNoteEntity = TopNote.builder()
                        .spices(perfumeModifyRequest.getTopNote())
                        .perfume(modifyPerfumeEntity)
                        .build();
                topNoteRepository.save(newTopNoteEntity);
            }
        }

        // MiddleNote 수정
        if (perfumeModifyRequest.getMiddleNote() != null) {
            MiddleNote targetMiddleNoteEntity = middleNoteRepository.findByPerfumeId(modifyPerfumeEntity.getId());
            if (targetMiddleNoteEntity != null) {
                MiddleNote modifyMiddleNoteEntity = targetMiddleNoteEntity.toBuilder()
                        .spices(perfumeModifyRequest.getMiddleNote())
                        .build();
                middleNoteRepository.save(modifyMiddleNoteEntity);
            } else {
                MiddleNote newMiddleNoteEntity = MiddleNote.builder()
                        .spices(perfumeModifyRequest.getMiddleNote())
                        .perfume(modifyPerfumeEntity)
                        .build();
                middleNoteRepository.save(newMiddleNoteEntity);
            }
        }

        // BaseNote 수정
        if (perfumeModifyRequest.getBaseNote() != null) {
            BaseNote targetBaseNoteEntity = baseNoteRepository.findByPerfumeId(modifyPerfumeEntity.getId());
            if (targetBaseNoteEntity != null) {
                BaseNote modifyBaseNoteEntity = targetBaseNoteEntity.toBuilder()
                        .spices(perfumeModifyRequest.getBaseNote())
                        .build();
                baseNoteRepository.save(modifyBaseNoteEntity);
            } else {
                BaseNote newBaseNoteEntity = BaseNote.builder()
                        .spices(perfumeModifyRequest.getBaseNote())
                        .perfume(modifyPerfumeEntity)
                        .build();
                baseNoteRepository.save(newBaseNoteEntity);
            }
        }
    }

    /**
     * 향수 정보 삭제
     */
    public void deletePerfume(Long perfumeId) {
        perfumeRepository.deleteById(perfumeId);
    }

    /**
     * 향수 추가 기능
     */
    public void createPerfume(PerfumeCreateRequest perfumeCreateRequest) {
        // 새로운 Perfume 엔티티 생성
        Perfume newPerfume = perfumeCreateRequest.toPerfumeEntity();
        perfumeRepository.save(newPerfume);

        // PerfumeImage 생성
        if (perfumeCreateRequest.getImageUrl() != null) {
            PerfumeImage newPerfumeImage = perfumeCreateRequest.toPerfumeImageEntity();
            perfumeImageRepository.save(newPerfumeImage);
        }

        // 싱글노트 향수일 시
        if (perfumeCreateRequest.getSingleNote() != null) {
            if (perfumeCreateRequest.getTopNote() != null ||
                    perfumeCreateRequest.getMiddleNote() != null ||
                    perfumeCreateRequest.getBaseNote() != null) {
                throw new IllegalArgumentException("싱글 노트와 다른 종류의 노트가 공존할 수 없습니다.");
            } else {
                SingleNote newSingleNote = perfumeCreateRequest.toSingleNoteEntity();
                singleNoteRepository.save(newSingleNote);
            }
        }

        // 멀티노트 향수일 시
        if (perfumeCreateRequest.getTopNote() != null &&
                perfumeCreateRequest.getMiddleNote() != null &&
                perfumeCreateRequest.getBaseNote() != null) {
            TopNote newTopNote = perfumeCreateRequest.toTopNoteEntity();
            topNoteRepository.save(newTopNote);
            MiddleNote newMiddleNote = perfumeCreateRequest.toMiddleNoteEntity();
            middleNoteRepository.save(newMiddleNote);
            BaseNote newBaseNote = perfumeCreateRequest.toBaseNoteEntity();
            baseNoteRepository.save(newBaseNote);
        }
    }
}
