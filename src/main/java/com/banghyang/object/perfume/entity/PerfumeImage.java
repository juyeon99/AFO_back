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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfume_id", nullable = false)
    @JsonIgnore // 순환 참조 방지
    private Perfume perfume;

    @Builder(toBuilder = true)
    public PerfumeImage(String url, Perfume perfume) {
        this.url = url;
        this.perfume = perfume;
    }

    public PerfumeImage modify(PerfumeImage modifyPerfumeImageEntity) {
        this.url = modifyPerfumeImageEntity.url;
        this.perfume = modifyPerfumeImageEntity.perfume;
        return this;
    }
}
