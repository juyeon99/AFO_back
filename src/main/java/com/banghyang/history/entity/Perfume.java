package com.banghyang.history.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@NoArgsConstructor
@Getter
public class Perfume {

    @Id
    private Long id;
    private String perfumeBrand;
    private String perfumeGrade;
    private Long perfumeId;
    private String perfumeImageUrl;
    private String perfumeName;
//    private Reason reason;
//    private Situation situation;
}
