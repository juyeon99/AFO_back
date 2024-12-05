package com.banghyang.recommend.repository;

import com.banghyang.recommend.entity.Chat;
import com.banghyang.recommend.entity.ChatHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatHistoryRepository extends MongoRepository<ChatHistory, String> {

    Optional<ChatHistory> findById(String id);

    List<ChatHistory> findByMemberIdOrderByTimeStampDesc(Long memberId);

}
