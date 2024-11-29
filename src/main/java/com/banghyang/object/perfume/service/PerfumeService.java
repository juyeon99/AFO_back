package com.banghyang.object.perfume.service;

import com.banghyang.object.note.entity.BaseNote;
import com.banghyang.object.note.entity.MiddleNote;
import com.banghyang.object.note.entity.SingleNote;
import com.banghyang.object.note.entity.TopNote;
import com.banghyang.object.note.repository.BaseNoteRepository;
import com.banghyang.object.note.repository.MiddleNoteRepository;
import com.banghyang.object.note.repository.SingleNoteRepository;
import com.banghyang.object.note.repository.TopNoteRepository;
import com.banghyang.object.perfume.dto.MultiPerfumeResponse;
import com.banghyang.object.perfume.dto.PerfumeCreateRequest;
import com.banghyang.object.perfume.dto.PerfumeModifyRequest;
import com.banghyang.object.perfume.dto.SinglePerfumeResponse;
import com.banghyang.object.perfume.entity.Perfume;
import com.banghyang.object.perfume.entity.PerfumeImage;
import com.banghyang.object.perfume.repository.PerfumeImageRepository;
import com.banghyang.object.perfume.repository.PerfumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
     * @return 모든 향수 response
     */
    public List<Object> getAllPerfumeResponses() {
        // 모든 퍼퓸 엔티티 가져오기
        List<Perfume> allPerfumeEntityList = perfumeRepository.findAll();

        // Response List 만들기
        List<Object> allPerfumeResponses = allPerfumeEntityList.stream().map(perfume -> {
                    // 향수 아이디로 향수 이미지 찾아오기
                    PerfumeImage perfumeImageEntity = perfumeImageRepository.findByPerfumeId(perfume.getId());

                    // 향수 아이디로 SingleNote 찾아보기
                    SingleNote singleNoteEntity = singleNoteRepository.findByPerfumeId(perfume.getId());

                    // 만약 해당하는 싱글노트 엔티티가 있다면 singlePerfumeResponse 반환
                    if (singleNoteEntity != null && perfumeImageEntity != null) {
                        return new SinglePerfumeResponse().from(perfume, perfumeImageEntity, singleNoteEntity);
                    }

                    // 향수 아이디로 탑, 미들, 베이스 노트 찾아보기
                    TopNote topNoteEntity = topNoteRepository.findByPerfumeId(perfume.getId());
                    MiddleNote middleNoteEntity = middleNoteRepository.findByPerfumeId(perfume.getId());
                    BaseNote baseNoteEntity = baseNoteRepository.findByPerfumeId(perfume.getId());

                    // 탑, 미들, 베이스 노트가 모두 존재하면 multiPerfumeResponse 반환
                    if (topNoteEntity != null &&
                            middleNoteEntity != null &&
                            baseNoteEntity != null &&
                            perfumeImageEntity != null) {
                        return new MultiPerfumeResponse()
                                .from(
                                        perfume,
                                        perfumeImageEntity,
                                        topNoteEntity,
                                        middleNoteEntity,
                                        baseNoteEntity
                                );
                    }

                    // 위 조건들을 충족하지 못하면 null 반환
                    return null;

                    // 필터로 null 은 제외하고 리스트로 변환
                }).filter(Objects::nonNull)
                // 이름을 기준으로 정렬
                .sorted(Comparator.comparing(perfumeResponse -> {
                    // single 과 multi 인지 판별하여 각 response 에 맞게 getName
                    if (perfumeResponse instanceof SinglePerfumeResponse) {
                        return ((SinglePerfumeResponse) perfumeResponse).getName();
                    } else {
                        return ((MultiPerfumeResponse) perfumeResponse).getName();
                    }
                }))
                .toList();

        return allPerfumeResponses;
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
