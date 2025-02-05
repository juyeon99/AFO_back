package com.banghyang.object.line.service;

import com.banghyang.object.line.entity.Line;
import com.banghyang.object.line.repository.LineRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class LineService {

    private final LineRepository lineRepository;

    public Line getLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("[LineService:getLineById] 아이디에 해당하는 계열 정보를 찾을 수 없습니다.")
        );
    }
}
