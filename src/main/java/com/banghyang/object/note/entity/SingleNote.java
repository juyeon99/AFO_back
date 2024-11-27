package com.banghyang.object.note.entity;

import com.banghyang.object.perfume.entity.Perfume;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SingleNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String spices;

    @OneToOne
    @JoinColumn(name = "perfume_id", unique = true, nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Perfume perfume;

    @Builder(toBuilder = true)
    public SingleNote(String spices, Perfume perfume) {
        this.spices = spices;
        this.perfume = perfume;
    }
}
