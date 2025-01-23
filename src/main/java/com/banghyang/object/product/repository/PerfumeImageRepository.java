package com.banghyang.object.product.repository;

import com.banghyang.object.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfumeImageRepository extends JpaRepository<ProductImage, Long> {
}
