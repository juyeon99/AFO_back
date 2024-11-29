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

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @Builder(toBuilder = true)
    public Spice(String name, String nameKr, String description, Line line) {
        this.name = name;
        this.nameKr = nameKr;
        this.description = description;
        this.line = line;
    }
}
