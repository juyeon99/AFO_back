package com.banghyang.object.like.repository;

import com.banghyang.object.like.entity.Like;
import com.banghyang.object.review.entity.Review;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LikeRepository extends CrudRepository<Like, Long> {
    List<Like> findByReview(Review review);
}
