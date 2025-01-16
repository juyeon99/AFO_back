package com.banghyang.object.spice.entity;

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

    @ManyToOne
    @JoinColumn(name = "spice_id", nullable = false)
    private Spice spice;

    @Builder
    public SpiceImage(String url, Spice spice) {
        this.url = url;
        this.spice = spice;
    }

    public void modify(SpiceImage modifySpiceImageEntity) {
        this.url = modifySpiceImageEntity.url;
        this.spice = modifySpiceImageEntity.spice;
    }
}
