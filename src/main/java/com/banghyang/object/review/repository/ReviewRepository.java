package com.banghyang.object.review.repository;

import com.banghyang.object.review.entity.Review;
import org.springframework.data.repository.CrudRepository;

public interface ReviewRepository extends CrudRepository<Review, Long> {
}
