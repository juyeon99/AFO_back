package com.banghyang.history.repository;

import com.banghyang.history.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {

    List<History> findByMemberIdOrderByTimeStampDesc(Long memberId);
}
