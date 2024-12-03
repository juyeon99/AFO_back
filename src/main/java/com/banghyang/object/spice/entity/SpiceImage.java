package com.banghyang.object.spice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpiceImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spice_id", nullable = false)
    @JsonIgnore // 순환참조 방지
    private Spice spice;

    @Builder(toBuilder = true)
    public SpiceImage(Long id, String url, Spice spice) {
        this.id = id;
        this.url = url;
        this.spice = spice;
    }
}
