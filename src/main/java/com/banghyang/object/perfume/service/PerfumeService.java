package com.banghyang.object.perfume.service;

import com.banghyang.common.mapper.Mapper;
import com.banghyang.common.type.NoteType;
import com.banghyang.common.util.ValidUtils;
import com.banghyang.object.note.entity.Note;
import com.banghyang.object.note.repository.NoteRepository;
import com.banghyang.object.noteSpice.entity.NoteSpice;
import com.banghyang.object.noteSpice.repository.NoteSpiceRepository;
import com.banghyang.object.perfume.dto.PerfumeCreateRequest;
import com.banghyang.object.perfume.dto.PerfumeModifyRequest;
import com.banghyang.object.perfume.dto.PerfumeResponse;
import com.banghyang.object.perfume.entity.Perfume;
import com.banghyang.object.perfume.entity.PerfumeImage;
import com.banghyang.object.perfume.repository.PerfumeImageRepository;
import com.banghyang.object.perfume.repository.PerfumeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PerfumeService {

    private final PerfumeRepository perfumeRepository;
    private final PerfumeImageRepository perfumeImageRepository;
    private final NoteRepository noteRepository;
    private final NoteSpiceRepository noteSpiceRepository;

    /**
     * @return 모든 향수 response 리스트(name 기준 오름차순 정렬)
     */
    @Cacheable(value = "perfumes") // 캐싱 사용
    public List<PerfumeResponse> getAllPerfumeResponses() {
        // perfume 엔티티 전체 가져와서 리스트에 담기
        List<Perfume> perfumeEntityList = perfumeRepository.findAll();

        return perfumeEntityList.stream() // 엔티티 리스트의 모든 항목에 stream 으로 접근
                .map(Mapper::mapPerfumeEntityToResponse) // mapper 메소드를 이용하여 response 로 변환
                .sorted(Comparator.comparing(PerfumeResponse::getNameEn, String.CASE_INSENSITIVE_ORDER)) // 이름순 정렬하여
                .toList(); // 리스트에 담아서 반환

        // 페이징 처리에서 캐싱 사용으로 변경함(추후 개선 사항 - 페이징, 캐싱 둘다 사용)
        // 페이지 번호 조정 및 정렬 설정
//        pageable = PageRequest.of(
//                pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() -1, // 0보다 크면 1 빼기 (1 시작을 위해)
//                pageable.getPageSize(),
//                Sort.by("name").ascending()
//        );

        // 페이징 된 엔티티 가져오기
//        Page<Perfume> perfumeEntityPage = perfumeRepository.findAll(pageable);
//        return perfumeEntityPage.map(Mapper::mapPerfumeEntityToResponse);
    }

    /**
     * 새로운 향수 정보 생성 메소드(향수, 향수이미지, 노트)
     */
    @CacheEvict(value = "perfumes") // 수정 시 마다 캐시데이터 함께 업데이트
    public void createPerfume(PerfumeCreateRequest perfumeCreateRequest) {
        // 새로운 향수 정보 담을 perfume 엔티티 생성
        // Mapper 클래스의 request -> entity 변환 메소드로 정보 담아줌
        Perfume newPerfumeEntity = Mapper.mapPerfumeCreateRequestToEntity(perfumeCreateRequest);
        // 정보 담은 엔티티 저장
        perfumeRepository.save(newPerfumeEntity);

        // 향수 이미지
        if (!perfumeCreateRequest.getImageUrls().isEmpty()) {
            // 만약 request 에 이미지 url 정보가 담겨있다면 이미지 엔티티 생성 진행
            perfumeCreateRequest.getImageUrls().forEach(imageUrl -> {
                PerfumeImage newPerfumeImageEntity = PerfumeImage.builder()
                        .perfume(newPerfumeEntity)
                        .url(imageUrl)
                        .build();
                perfumeImageRepository.save(newPerfumeImageEntity);
            });
        }

        // 노트
        if (!perfumeCreateRequest.getSingleNote().isEmpty()) { // 싱글노트가 존재하고,
            if (perfumeCreateRequest.getTopNote().isEmpty() &&
                    perfumeCreateRequest.getMiddleNote().isEmpty() &&
                    perfumeCreateRequest.getBaseNote().isEmpty()) { // 나머지 노트들이 다 null 일때
                // 새로운 조인 테이블 생성
                NoteSpice newSingleSpice = NoteSpice.builder().build();
                noteSpiceRepository.save(newSingleSpice);

                // 싱글노트 엔티티 생성
                Note newSingleNoteEntity = Note.builder()
                        .noteType(NoteType.SINGLE)
                        .perfume(newPerfumeEntity)
                        .noteSpice(newSingleSpice)
                        .build();
                noteRepository.save(newSingleNoteEntity);

                perfumeCreateRequest.getSingleNote().forEach(note -> {

                })

//                SingleNote newSingleNoteEntity = SingleNote.builder()
//                        .perfume(newPerfumeEntity)
//                        .spices(perfumeCreateRequest.getSingleNote())
//                        .build();
//                singleNoteRepository.save(newSingleNoteEntity); // 싱글노트 엔티티 생성하고 향수 엔티티와 연결
            } else {
                // 만약 싱글 노트와 다른 노트가 동시에 존재하면 예외 발생시키기
                throw new IllegalArgumentException("싱글 노트와 다른 종류의 노트가 동시에 존재할 수 없습니다.");
            }
//        } else { // 싱글노트가 존재하지 않는 경우 탑, 미들, 베이스 노트 생성 진행
            // 탑노트
//            if (ValidUtils.isNotBlank(perfumeCreateRequest.getTopNote())) {
//                TopNote newTopNoteEntity = TopNote.builder()
//                        .perfume(newPerfumeEntity)
//                        .spices(perfumeCreateRequest.getTopNote())
//                        .build();
//                topNoteRepository.save(newTopNoteEntity);
//            }

            // 미들노트
//            if (ValidUtils.isNotBlank(perfumeCreateRequest.getMiddleNote())) {
//                MiddleNote newMiddleNoteEntity = MiddleNote.builder()
//                        .perfume(newPerfumeEntity)
////                        .spices(perfumeCreateRequest.getMiddleNote())
//                        .build();
//                middleNoteRepository.save(newMiddleNoteEntity);
//            }

            // 베이스노트
//            if (ValidUtils.isNotBlank(perfumeCreateRequest.getBaseNote())) {
//                BaseNote newBaseNoteEntity = BaseNote.builder()
//                        .perfume(newPerfumeEntity)
////                        .spices(perfumeCreateRequest.getBaseNote())
//                        .build();
//                baseNoteRepository.save(newBaseNoteEntity);
//            }

//            if (!ValidUtils.isNotBlank(perfumeCreateRequest.getTopNote()) &&
//                    !ValidUtils.isNotBlank(perfumeCreateRequest.getMiddleNote()) &&
//                    !ValidUtils.isNotBlank(perfumeCreateRequest.getBaseNote())) {
//                // 노트가 아무것도 존재하지 않을 시 예외 발생시키기
//                throw new IllegalArgumentException("노트 정보가 존재하지 않아 향수 등록을 실패했습니다.");
//    }
        }
    }

    /**
     * 향수 정보 수정 메소드
     */
    @CacheEvict(value = "perfumes") // 수정 시 마다 캐시데이터 함께 업데이트
    public void modifyPerfume(PerfumeModifyRequest perfumeModifyRequest) {
        // 수정할 perfume 엔티티 request 의 id 값으로 찾아오기
        Perfume targetPerfumeEntity = perfumeRepository.findById(perfumeModifyRequest.getId())
                .orElseThrow(() -> new IllegalArgumentException("수정하려는 향수의 정보를 찾을 수 없습니다."));
        // 찾아온 perfume 엔티티 수정
        Perfume modifyPerfumeEntity = Perfume.builder()
//                .name(perfumeModifyRequest.getName())
                .description(perfumeModifyRequest.getDescription())
                .brand(perfumeModifyRequest.getBrand())
                .grade(perfumeModifyRequest.getGrade())
                .build();
        // 향수 엔티티 클래스에 만들어둔 수정 메소드로 수정 적용
        targetPerfumeEntity.modify(modifyPerfumeEntity);

        // 향수 이미지
        if (ValidUtils.isNotBlank(perfumeModifyRequest.getImageUrl())) {
            // request 에 이미지 URL 존재할 시 이미지 수정 진행
//            PerfumeImage targetPerfumeImageEntity = targetPerfumeEntity.getPerfumeImage();
            // 수정 이미지 엔티티 생성
            PerfumeImage modifyPerfumeImageEntity = PerfumeImage.builder()
                    .url(perfumeModifyRequest.getImageUrl())
                    .perfume(targetPerfumeEntity)
                    .build();

//            if (targetPerfumeImageEntity != null) {
//                // 기존 엔티티가 있다면 수정
//                targetPerfumeImageEntity.modify(modifyPerfumeImageEntity);
//            } else {
//                // 기존 엔티티가 없다면 생성
//                perfumeImageRepository.save(modifyPerfumeImageEntity);
//            }
//        } else {
//            if (targetPerfumeEntity.getPerfumeImage() != null) {
//                perfumeImageRepository.delete(targetPerfumeEntity.getPerfumeImage());
//            }
        }

        // 노트
//        if (ValidUtils.isNotBlank(perfumeModifyRequest.getSingleNote())) {
//            if (!ValidUtils.isNotBlank(perfumeModifyRequest.getTopNote()) &&
//                    !ValidUtils.isNotBlank(perfumeModifyRequest.getMiddleNote()) &&
//                    !ValidUtils.isNotBlank(perfumeModifyRequest.getBaseNote())) {
        // request 에 싱글노트가 존재하고 다른 노트가 없을 시 싱글노트 수정 진행
//                SingleNote modifySingleNoteEntity = SingleNote.builder()
//                        .perfume(targetPerfumeEntity)
//                        .spices(perfumeModifyRequest.getSingleNote())
//                        .build();

//                if (targetPerfumeEntity.getSingleNote() != null) {
//                    targetPerfumeEntity.getSingleNote().modify(modifySingleNoteEntity);
//                } else {
//                    singleNoteRepository.save(modifySingleNoteEntity);
//                }

        // 싱글노트 외의 다른 노트 엔티티 모두 삭제
        // 탑노트 삭제
//                if (targetPerfumeEntity.getTopNote() != null) {
//                    topNoteRepository.delete(targetPerfumeEntity.getTopNote());
//                }
//                // 미들노트 삭제
//                if (targetPerfumeEntity.getMiddleNote() != null) {
//                    middleNoteRepository.delete(targetPerfumeEntity.getMiddleNote());
//                }
//                // 베이스노트 삭제
//                if (targetPerfumeEntity.getBaseNote() != null) {
//                    baseNoteRepository.delete(targetPerfumeEntity.getBaseNote());
//                }
//            } else {
        // request 에 싱글노트와 다른 노트가 함께 존재시 예외 발생시키기
//                throw new IllegalArgumentException("싱글 노트와 다른 종류의 노트가 동시에 존재할 수 없습니다.");
//            }
//        } else { // 싱글노트가 존재하지 않을 시 싱글 노트 엔티티 삭제하고 탑, 미들, 베이스 노트 수정 진행
        // 탑노트
//            if (ValidUtils.isNotBlank(perfumeModifyRequest.getTopNote())) {
        // 싱글노트 존재 시 삭제하기
//                if (targetPerfumeEntity.getSingleNote() != null) {
//                    singleNoteRepository.delete(targetPerfumeEntity.getSingleNote());
//                }

//                TopNote modifyTopNoteEntity = TopNote.builder()
//                        .perfume(targetPerfumeEntity)
//                        .spices(perfumeModifyRequest.getTopNote())
//                        .build();

//                if (targetPerfumeEntity.getTopNote() != null) {
//                    targetPerfumeEntity.getTopNote().modify(modifyTopNoteEntity);
//                } else {
//                    topNoteRepository.save(modifyTopNoteEntity);
//                }
//            } else {
        // 탑노트 입력 정보가 없다면 기존 탑노트 존재 검증 후 삭제 처리
//                if (targetPerfumeEntity.getTopNote() != null) {
//                    topNoteRepository.delete(targetPerfumeEntity.getTopNote());
//                }
//            }
        // 미들노트
//            if (ValidUtils.isNotBlank(perfumeModifyRequest.getMiddleNote())) {
        // 싱글노트 존재 시 삭제하기
//                if (targetPerfumeEntity.getSingleNote() != null) {
//                    singleNoteRepository.delete(targetPerfumeEntity.getSingleNote());
//                }

//                MiddleNote modifyMiddleNoteEntity = MiddleNote.builder()
//                        .perfume(targetPerfumeEntity)
//                        .spices(perfumeModifyRequest.getMiddleNote())
//                        .build();

//                if (targetPerfumeEntity.getMiddleNote() != null) {
//                    targetPerfumeEntity.getMiddleNote().modify(modifyMiddleNoteEntity);
//                } else {
//                    middleNoteRepository.save(modifyMiddleNoteEntity);
//                }
//            } else {
//                // 미들노트 입력 정보가 없다면 기존 미들노트 존재 검증 후 삭제 처리
//                if (targetPerfumeEntity.getMiddleNote() != null) {
//                    middleNoteRepository.delete(targetPerfumeEntity.getMiddleNote());
//                }
//            }
        // 베이스노트
//            if (ValidUtils.isNotBlank(perfumeModifyRequest.getBaseNote())) {
        // 싱글노트 존재 시 삭제하기
//                if (targetPerfumeEntity.getSingleNote() != null) {
//                    singleNoteRepository.delete(targetPerfumeEntity.getSingleNote());
//                }

//                BaseNote modifyBaseNoteEntity = BaseNote.builder()
//                        .perfume(targetPerfumeEntity)
//                        .spices(perfumeModifyRequest.getBaseNote())
//                        .build();

//                if (targetPerfumeEntity.getBaseNote() != null) {
//                    targetPerfumeEntity.getBaseNote().modify(modifyBaseNoteEntity);
//                } else {
//                    baseNoteRepository.save(modifyBaseNoteEntity);
//                }
//            } else {
//                // 베이스노트 입력 정보가 없다면 기존 베이스노트 존재 검증 후 삭제 처리
//                if (targetPerfumeEntity.getBaseNote() != null) {
//                    baseNoteRepository.delete(targetPerfumeEntity.getBaseNote());
//                }
//            }
//        }
    }

    /**
     * 향수 삭제 메소드
     */
    @CacheEvict(value = "perfumes") // 수정 시 마다 캐시데이터 함께 업데이트
    public void deletePerfume(Long perfumeId) {
        Perfume targetPerfumeEntity = perfumeRepository.findById(perfumeId)
                .orElseThrow(() -> new IllegalArgumentException("삭제하려는 향수의 정보를 찾을 수 업습니다."));
        perfumeRepository.delete(targetPerfumeEntity);
    }
}
