package com.banghyang.object.mapper;

import com.banghyang.member.dto.MemberResponse;
import com.banghyang.member.entity.Member;
import com.banghyang.object.line.entity.Line;
import com.banghyang.object.perfume.dto.PerfumeCreateRequest;
import com.banghyang.object.perfume.dto.PerfumeResponse;
import com.banghyang.object.perfume.entity.Perfume;
import com.banghyang.object.spice.dto.SpiceCreateRequest;
import com.banghyang.object.spice.dto.SpiceResponse;
import com.banghyang.object.spice.entity.Spice;

public class Mapper {
    /**
     * 멤버 엔티티에서 멤버 조회 response 로 변환하는 매퍼
     */
    public static MemberResponse mapMemberEntityToResponse(Member member) {
        MemberResponse memberResponse = new MemberResponse(); // 내용 담을 response 생성
        memberResponse.setEmail(member.getEmail());
        memberResponse.setName(member.getName());
        memberResponse.setGender(member.getGender());
        memberResponse.setBirthyear(member.getBirthyear());
        memberResponse.setRole(member.getRole());
        memberResponse.setCreatedAt(member.getCreatedAt());
        return memberResponse;
    }

    /**
     * 향수 엔티티에서 향수 조회 response 로 변환하는 매퍼
     */
    public static PerfumeResponse mapPerfumeEntityToResponse(Perfume perfumeEntity) {
        PerfumeResponse perfumeResponse = new PerfumeResponse(); // 내용 담을 response 생성
        perfumeResponse.setId(perfumeEntity.getId()); // 향수 id 담기
        perfumeResponse.setName(perfumeEntity.getName()); // 향수 이름 담기
        perfumeResponse.setDescription(perfumeEntity.getDescription()); // 향수 설명 담기
        perfumeResponse.setBrand(perfumeEntity.getBrand()); // 브랜드명 담기
        perfumeResponse.setGrade(perfumeEntity.getGrade()); // 부향률 담기

        // 이미지 존재시에 담고, 없을시 null
        perfumeResponse.setImageUrl(perfumeEntity.getPerfumeImage() != null ?
                perfumeEntity.getPerfumeImage().getUrl() : null);

        // 싱글노트 존재시에 담고, 없을시 null
        perfumeResponse.setSingleNote(perfumeEntity.getSingleNote() != null ?
                perfumeEntity.getSingleNote().getSpices() : null);

        // 탑노트
        perfumeResponse.setTopNote(perfumeEntity.getTopNote() != null ?
                perfumeEntity.getTopNote().getSpices() : null);

        // 미들노트
        perfumeResponse.setMiddleNote(perfumeEntity.getMiddleNote() != null ?
                perfumeEntity.getMiddleNote().getSpices() : null);

        // 베이스노트
        perfumeResponse.setBaseNote(perfumeEntity.getBaseNote() != null ?
                perfumeEntity.getBaseNote().getSpices() : null);

        return perfumeResponse;
    }

    /**
     * 향수 생성 request 를 향수 엔티티로 변환하는 매퍼
     */
    public static Perfume mapPerfumeCreateRequestToEntity(PerfumeCreateRequest perfumeCreateRequest) {
        if (perfumeCreateRequest.getName() != null &&
                perfumeCreateRequest.getDescription() != null &&
                perfumeCreateRequest.getBrand() != null &&
                perfumeCreateRequest.getGrade() != null) {
            // 이름, 설명, 브랜드, 등급 정보가 모두 있어야 perfume 반환
            return Perfume.builder()
                    .name(perfumeCreateRequest.getName())
                    .description(perfumeCreateRequest.getDescription())
                    .brand(perfumeCreateRequest.getBrand())
                    .grade(perfumeCreateRequest.getGrade())
                    .build();
        } else {
            // 정보 누락되어있으면 exception 발생
            throw new IllegalArgumentException("향수 등록에 필요한 필수 정보가 누락되었습니다. (이름, 설명, 브랜드, 등급)");
        }
    }

    /**
     * 향료 엔티티에서 향료 조회 response 로 변환하는 매퍼
     */
    public static SpiceResponse mapSpiceEntityToResponse(Spice spiceEntity) {
        SpiceResponse spiceResponse = new SpiceResponse(); // 내용 담을 response 생성
        spiceResponse.setId(spiceEntity.getId()); // id
        spiceResponse.setName(spiceEntity.getName()); // 영문명
        spiceResponse.setNameKr(spiceEntity.getNameKr()); // 한글명
        spiceResponse.setDescription(spiceEntity.getDescription()); // 설명
        spiceResponse.setLine(spiceEntity.getLine().getName()); // 계열명
        spiceResponse.setColor(spiceEntity.getLine().getColor()); // 색상코드

        // 이미지 존재시에 담고, 없을시 null
        spiceResponse.setImageUrl(spiceEntity.getSpiceImage() != null ?
                spiceEntity.getSpiceImage().getUrl() : null);

        return spiceResponse;
    }

    public static Spice mapSpiceCreateRequestToEntity(SpiceCreateRequest spiceCreateRequest, Line lineEntity) {
        if (spiceCreateRequest.getName() != null &&
        spiceCreateRequest.getNameKr() != null &&
        spiceCreateRequest.getDescription() != null) {
            // 영문명, 한글명, 설명 모두 있어야 entity 반환
            return Spice.builder()
                    .name(spiceCreateRequest.getName())
                    .nameKr(spiceCreateRequest.getNameKr())
                    .description(spiceCreateRequest.getDescription())
                    .line(lineEntity)
                    .build();
        } else {
            // 정보 누락시 예외 발생시키기
            throw new IllegalArgumentException("향료 등록에 필요한 정보가 누락되었습니다. (영문명, 한글명, 설명)");
        }
    }
}
