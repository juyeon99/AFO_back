package com.banghyang.object.perfume.service;

import com.banghyang.object.note.entity.BaseNote;
import com.banghyang.object.note.entity.MiddleNote;
import com.banghyang.object.note.entity.SingleNote;
import com.banghyang.object.note.entity.TopNote;
import com.banghyang.object.note.repository.BaseNoteRepository;
import com.banghyang.object.note.repository.MiddleNoteRepository;
import com.banghyang.object.note.repository.SingleNoteRepository;
import com.banghyang.object.note.repository.TopNoteRepository;
import com.banghyang.object.perfume.dto.PerfumeCreateRequest;
import com.banghyang.object.perfume.dto.PerfumeResponse;
import com.banghyang.object.perfume.entity.Perfume;
import com.banghyang.object.perfume.entity.PerfumeImage;
import com.banghyang.object.perfume.mapper.Mapper;
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
        Perfume newPerfumeEntity = Mapper.mapPerfumeCreateRequestToEntity(perfumeCreateRequest);
        perfumeRepository.save(newPerfumeEntity);

        if (perfumeCreateRequest.getImageUrl() != null) {
            PerfumeImage newPerfumeImageEntity = PerfumeImage.builder()
                    .perfume(newPerfumeEntity)
                    .url(perfumeCreateRequest.getImageUrl())
                    .build();
            perfumeImageRepository.save(newPerfumeImageEntity);
        }

        if (perfumeCreateRequest.getSingleNote() != null) {
            if (perfumeCreateRequest.getTopNote() == null &&
                    perfumeCreateRequest.getMiddleNote() == null &&
                    perfumeCreateRequest.getBaseNote() == null) {
                SingleNote newSingleNoteEntity = SingleNote.builder()
                        .perfume(newPerfumeEntity)
                        .spices(perfumeCreateRequest.getSingleNote())
                        .build();
                singleNoteRepository.save(newSingleNoteEntity);
            } else {
                throw new IllegalArgumentException("싱글 노트와 다른 종류의 노트가 동시에 존재할 수 없습니다.");
            }
        } else {
            if (perfumeCreateRequest.getTopNote() != null) {
                TopNote newTopNoteEntity = TopNote.builder()
                        .perfume(newPerfumeEntity)
                        .spices(perfumeCreateRequest.getTopNote())
                        .build();
                topNoteRepository.save(newTopNoteEntity);
            } else if (perfumeCreateRequest.getMiddleNote() != null) {
                MiddleNote newMiddleNoteEntity = MiddleNote.builder()
                        .perfume(newPerfumeEntity)
                        .spices(perfumeCreateRequest.getMiddleNote())
                        .build();
                middleNoteRepository.save(newMiddleNoteEntity);
            } else if (perfumeCreateRequest.getBaseNote() != null) {
                BaseNote newBaseNoteEntity = BaseNote.builder()
                        .perfume(newPerfumeEntity)
                        .spices(perfumeCreateRequest.getBaseNote())
                        .build();
                baseNoteRepository.save(newBaseNoteEntity);
            }
        }
    }
}
