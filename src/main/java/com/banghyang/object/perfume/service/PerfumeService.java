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

    public List<Object> getAllPerfumes() {
        // 모든 퍼퓸 엔티티 가져오기
        List<Perfume> perfumes = perfumeRepository.findAll();

        // Response List 만들기
        List<Object> perfumeResponses = perfumes.stream().map(perfume -> {
            // 향수 아이디로 향수 이미지 찾아오기
            PerfumeImage perfumeImage = perfumeImageRepository.findByPerfumeId(perfume.getId());

            // 향수 아이디로 SingleNote 찾아보기
            SingleNote singleNote = singleNoteRepository.findByPerfumeId(perfume.getId());

            // 만약 해당하는 싱글노트 엔티티가 있다면 singlePerfumeResponse 반환
            if (singleNote != null && perfumeImage != null) {
                return new SinglePerfumeResponse().from(perfume, perfumeImage, singleNote);
            }

            // 향수 아이디로 탑, 미들, 베이스 노트 찾아보기
            TopNote topNote = topNoteRepository.findByPerfumeId(perfume.getId());
            MiddleNote middleNote = middleNoteRepository.findByPerfumeId(perfume.getId());
            BaseNote baseNote = baseNoteRepository.findByPerfumeId(perfume.getId());

            // 탑, 미들, 베이스 노트가 모두 존재하면 multiPerfumeResponse 반환
            if (topNote != null && middleNote != null && baseNote != null && perfumeImage != null) {
                return new MultiPerfumeResponse().from(perfume, perfumeImage, topNote, middleNote, baseNote);
            }

            return null;
        }).filter(Objects::nonNull).toList();

        System.out.println(perfumeResponses.size());

        return perfumeResponses;
    }
}
