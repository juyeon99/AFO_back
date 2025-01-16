package com.banghyang.object.perfume.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerfumeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @ManyToOne
    @JoinColumn(name = "perfume_id", nullable = false)
    private Perfume perfume;

    @Builder
    public PerfumeImage(String url, Perfume perfume) {
        this.url = url;
        this.perfume = perfume;
    }

    public void modify(PerfumeImage modifyPerfumeImageEntity) {
        this.url = modifyPerfumeImageEntity.url;
        this.perfume = modifyPerfumeImageEntity.perfume;
    }
}
