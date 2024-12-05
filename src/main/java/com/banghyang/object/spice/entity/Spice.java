package com.banghyang.object.spice.entity;

import com.banghyang.object.line.entity.Line;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Spice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String nameKr;
    private String description;

    @OneToOne(mappedBy = "spice", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private SpiceImage spiceImage;

    @ManyToOne
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @Builder
    public Spice(String name, String nameKr, String description, Line line) {
        this.name = name;
        this.nameKr = nameKr;
        this.description = description;
        this.line = line;
    }

    public void modify(Spice modifySpiceEntity) {
        this.name = modifySpiceEntity.getName();
        this.nameKr = modifySpiceEntity.getNameKr();
        this.description = modifySpiceEntity.getDescription();
        this.line = modifySpiceEntity.getLine();
    }
}
