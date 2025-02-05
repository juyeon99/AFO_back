package com.banghyang.object.product.repository;

import com.banghyang.object.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    Product findByNameKr(String nameKr);
}
