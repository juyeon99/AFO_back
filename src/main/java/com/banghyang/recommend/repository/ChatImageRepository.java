package com.banghyang.recommend.repository;

import com.banghyang.recommend.entity.ChatImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatImageRepository extends JpaRepository<ChatImage, Long> {
}
