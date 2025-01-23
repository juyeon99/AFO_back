package com.banghyang.object.spice.entity;

import com.banghyang.object.line.entity.Line;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Spice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 향료 아이디

    private String nameEn; // 향료 영문명
    private String nameKr; // 향료 한글명
    private String descriptionEn; // 향료 영문설명
    private String descriptionKr; // 향료 한글설명

    @ManyToOne
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @Builder
    public Spice(String nameEn, String nameKr, String descriptionEn, String descriptionKr, Line line) {
        this.nameEn = nameEn;
        this.nameKr = nameKr;
        this.descriptionEn = descriptionEn;
        this.descriptionKr = descriptionKr;
        this.line = line;
    }

    public void modify(Spice modifySpiceEntity) {
        this.nameEn = modifySpiceEntity.getNameEn();
        this.nameKr = modifySpiceEntity.getNameKr();
        this.descriptionEn = modifySpiceEntity.getDescriptionEn();
        this.descriptionKr = modifySpiceEntity.getDescriptionKr();
        this.line = modifySpiceEntity.getLine();
    }
}
