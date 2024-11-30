package com.banghyang.object.perfume.service;

import com.banghyang.object.perfume.dto.PerfumeResponse;
import com.banghyang.object.perfume.entity.Perfume;
import com.banghyang.object.perfume.mapper.PerfumeMapper;
import com.banghyang.object.perfume.repository.PerfumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerfumeService {

    private final PerfumeRepository perfumeRepository;

    /**
     * @return 모든 향수 response 리스트(name 기준 오름차순 정렬)
     */
    public List<PerfumeResponse> getAllPerfumeResponses() {
        // perfume 엔티티 전체 가져와서 리스트에 담기
        List<Perfume> perfumeEntityList = perfumeRepository.findAll();

        return perfumeEntityList.stream() // 엔티티 리스트의 모든 항목에 stream 으로 접근
                .map(PerfumeMapper::mapPerfumeToResponse) // mapper 메소드를 이용하여 response 로 변환
                .sorted(Comparator.comparing(PerfumeResponse::getName, String.CASE_INSENSITIVE_ORDER)) // 이름순 정렬하여
                .toList(); // 리스트에 담아서 반환
    }
}
