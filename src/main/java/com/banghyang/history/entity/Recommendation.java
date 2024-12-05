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
public class Recommendation {

    @Id
    private Long id;
    private String perfumeName;
    private String perfumeBrand;
    private String perfumeImageUrl;
    private String perfumeGrade;
    private String reason;
    private String situation;


}
