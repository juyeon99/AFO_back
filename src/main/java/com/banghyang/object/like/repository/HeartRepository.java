package com.banghyang.object.like.repository;

import com.banghyang.object.like.entity.Heart;
import com.banghyang.object.review.entity.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HeartRepository extends CrudRepository<Heart, Long> {
    List<Heart> findByReview(Review review);
    void deleteByReview(Review review);
    @Query("SELECT h.review.id FROM Heart h WHERE h.member.id = :userId")
    List<Long> findLikedReviewIdsByUserId(Long userId);
    int deleteByReviewId(Long reviewId);
}
