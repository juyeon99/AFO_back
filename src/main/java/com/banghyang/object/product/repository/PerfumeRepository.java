package com.banghyang.object.product.repository;

import com.banghyang.object.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfumeRepository extends JpaRepository<Product, Long> {
}
