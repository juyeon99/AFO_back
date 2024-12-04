package com.banghyang.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatImageRepository extends JpaRepository<ChatImage, Long> {
    Optional<ChatImage> findByImageUrl(String s3ImageUrl);
}
