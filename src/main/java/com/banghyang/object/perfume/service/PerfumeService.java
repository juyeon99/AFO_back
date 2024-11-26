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
import com.banghyang.object.perfume.dto.SinglePerfumeResponse;
import com.banghyang.object.perfume.entity.Perfume;
import com.banghyang.object.perfume.entity.PerfumeImage;
import com.banghyang.object.perfume.repository.PerfumeImageRepository;
import com.banghyang.object.perfume.repository.PerfumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            if (topNoteEntity != null && middleNoteEntity != null && baseNoteEntity != null && perfumeImageEntity != null) {
                return new MultiPerfumeResponse().from(perfume, perfumeImageEntity, topNoteEntity, middleNoteEntity, baseNoteEntity);
            }

            // 위 조건들을 충족하지 못하면 null 반환
            return null;

            // 필터로 null 은 제외하고 리스트로 변환
        }).filter(Objects::nonNull).toList();

        return allPerfumeResponses;
    }
}
