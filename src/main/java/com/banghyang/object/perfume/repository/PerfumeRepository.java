package com.banghyang.object.perfume.repository;

import com.banghyang.object.perfume.entity.Perfume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfumeRepository extends JpaRepository<Perfume, Long> {
}
