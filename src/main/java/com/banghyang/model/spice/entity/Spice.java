package com.banghyang.model.spice.entity;

import com.banghyang.model.line.entity.Line;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
    private String name_kr;
    private String content;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;
}
