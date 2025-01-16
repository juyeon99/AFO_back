package com.banghyang.object.spice.entity;

import com.banghyang.object.line.entity.Line;
import com.banghyang.object.noteSpice.entity.NoteSpice;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Spice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameEn;
    private String nameKr;
    private String descriptionEn;
    private String descriptionKr;

    @ManyToOne
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @OneToMany(mappedBy = "spice", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SpiceImage> spiceImages;

    @ManyToOne
    @JoinColumn(name = "note_spice_id")
    private NoteSpice noteSpice;

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
