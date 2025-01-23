package com.banghyang.common.mapper;

import com.banghyang.chat.entity.Chat;
import com.banghyang.common.type.NoteType;
import com.banghyang.common.util.ValidUtils;
import com.banghyang.member.dto.MemberResponse;
import com.banghyang.member.entity.Member;
import com.banghyang.object.line.entity.Line;
import com.banghyang.object.product.dto.PerfumeCreateRequest;
import com.banghyang.object.product.dto.PerfumeResponse;
import com.banghyang.object.product.entity.Product;
import com.banghyang.object.product.entity.ProductImage;
import com.banghyang.object.spice.dto.SpiceCreateRequest;
import com.banghyang.object.spice.dto.SpiceResponse;
import com.banghyang.object.spice.entity.Spice;
import com.banghyang.object.spice.entity.SpiceImage;

import java.util.stream.Collectors;

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
    public static PerfumeResponse mapPerfumeEntityToResponse(Product productEntity) {
        PerfumeResponse perfumeResponse = new PerfumeResponse(); // 내용 담을 response 생성
        perfumeResponse.setId(productEntity.getId()); // 향수 id 담기
        perfumeResponse.setNameKr(productEntity.getNameKr()); // 향수 이름 담기
        perfumeResponse.setDescription(productEntity.getContent()); // 향수 설명 담기
        perfumeResponse.setBrand(productEntity.getBrand()); // 브랜드명 담기
        perfumeResponse.setGrade(productEntity.getGrade()); // 부향률 담기

        // 이미지 존재시에 담고, 없을시 null
        perfumeResponse.setImageUrls(productEntity.getProductImages() != null ?
                productEntity.getProductImages().stream().map(ProductImage::getUrl).toList() : null);

        // 싱글노트 존재시에 담고, 없을시 null
        perfumeResponse.setSingleNote(productEntity.getNotes().stream()
                .filter(note -> note.getNoteType() == NoteType.SINGLE)
                .findFirst()
                .map(note ->
                        note.getNoteSpice().getSpices().stream()
                                .map(Spice::getNameKr)
                                .collect(Collectors.joining(", "))
                ).orElse(null)
        );

        // 탑노트
        perfumeResponse.setTopNote(productEntity.getNotes().stream()
                .filter(note -> note.getNoteType() == NoteType.TOP)
                .findFirst()
                .map(note ->
                        note.getNoteSpice().getSpices().stream()
                                .map(Spice::getNameKr)
                                .collect(Collectors.joining(", "))
                ).orElse(null)
        );

        // 미들노트
        perfumeResponse.setMiddleNote(productEntity.getNotes().stream()
                .filter(note -> note.getNoteType() == NoteType.MIDDLE)
                .findFirst()
                .map(note ->
                        note.getNoteSpice().getSpices().stream()
                                .map(Spice::getNameKr)
                                .collect(Collectors.joining(", "))
                ).orElse(null)
        );

        // 베이스노트
        perfumeResponse.setBaseNote(productEntity.getNotes().stream()
                .filter(note -> note.getNoteType() == NoteType.BASE)
                .findFirst()
                .map(note ->
                        note.getNoteSpice().getSpices().stream()
                                .map(Spice::getNameKr)
                                .collect(Collectors.joining(", "))
                ).orElse(null)
        );

        return perfumeResponse;
    }

    /**
     * 향수 생성 request 를 향수 엔티티로 변환하는 매퍼
     */
    public static Product mapPerfumeCreateRequestToEntity(PerfumeCreateRequest perfumeCreateRequest) {
        if (ValidUtils.isNotBlank(perfumeCreateRequest.getNameKr()) &&
                ValidUtils.isNotBlank(perfumeCreateRequest.getBrand()) &&
                ValidUtils.isNotBlank(perfumeCreateRequest.getGrade()) &&
                ValidUtils.isNotBlank(perfumeCreateRequest.getSizeOption()) &&
                ValidUtils.isNotBlank(perfumeCreateRequest.getDescription()) &&
                ValidUtils.isNotBlank(perfumeCreateRequest.getMainAccord()) &&
                ValidUtils.isNotBlank(perfumeCreateRequest.getIngredients())) {
            // 이름, 설명, 브랜드, 등급 정보가 모두 있어야 perfume 반환
            return Product.builder()
                    .nameEn(perfumeCreateRequest.getNameEn())
                    .nameKr(perfumeCreateRequest.getNameKr())
                    .brand(perfumeCreateRequest.getBrand())
                    .grade(perfumeCreateRequest.getGrade())
                    .sizeOption(perfumeCreateRequest.getSizeOption())
                    .description(perfumeCreateRequest.getDescription())
                    .mainAccord(perfumeCreateRequest.getMainAccord())
                    .ingredients(perfumeCreateRequest.getIngredients())
                    .build();
        } else {
            // 정보 누락되어있으면 exception 발생
            throw new IllegalArgumentException("향수 등록에 필요한 필수 정보가 누락되었습니다.");
        }
    }

    /**
     * 향료 엔티티에서 향료 조회 response 로 변환하는 매퍼
     */
    public static SpiceResponse mapSpiceEntityToResponse(Spice spiceEntity) {
        SpiceResponse spiceResponse = new SpiceResponse(); // 내용 담을 response 생성
        spiceResponse.setId(spiceEntity.getId()); // id
        spiceResponse.setNameEn(spiceEntity.getNameEn()); // 영문명
        spiceResponse.setNameKr(spiceEntity.getNameKr()); // 한글명
        spiceResponse.setDescriptionKr(spiceEntity.getDescriptionKr()); // 한글설명

        spiceResponse.setLineId(spiceEntity.getLine().getId()); // 계열아이디(프론트에서 색깔 지정에 사용)
        spiceResponse.setLineName(spiceEntity.getLine().getName()); // 계열명

        // 이미지 존재시에 담고, 없을시 null
        spiceResponse.setImageUrl(spiceEntity.getSpiceImages() != null ?
                spiceEntity.getSpiceImages().stream().map(SpiceImage::getUrl).toList() : null);

        return spiceResponse;
    }

    /**
     * 향료 등록 request 를 엔티티로 변환하는 매퍼
     */
    public static Spice mapSpiceCreateRequestToEntity(SpiceCreateRequest spiceCreateRequest, Line lineEntity) {
        if (ValidUtils.isNotBlank(spiceCreateRequest.getNameEn()) &&
                ValidUtils.isNotBlank(spiceCreateRequest.getNameKr()) &&
                ValidUtils.isNotBlank(spiceCreateRequest.getDescriptionEn()) &&
                ValidUtils.isNotBlank(spiceCreateRequest.getDescriptionKr())) {
            // 영문명, 한글명, 설명 모두 있어야 entity 반환
            return Spice.builder()
                    .nameEn(spiceCreateRequest.getNameEn())
                    .nameKr(spiceCreateRequest.getNameKr())
                    .descriptionEn(spiceCreateRequest.getDescriptionEn())
                    .descriptionKr(spiceCreateRequest.getDescriptionKr())
                    .line(lineEntity)
                    .build();
        } else {
            // 정보 누락시 예외 발생시키기
            throw new IllegalArgumentException("향료 등록에 필요한 정보가 누락되었습니다. (영문명, 한글명, 설명)");
        }
    }

    // 향수 엔티티를 채팅기록의 추천향수정보로 변환하는 매퍼
    public static Chat.Recommendation mapPerfumeEntityToChatRecommendation(
            Product productEntity,
            String reason,
            String situation
    ) {
        Chat.Recommendation recommendation = new Chat.Recommendation();
        recommendation.setPerfumeName(productEntity.getNameKr());
        recommendation.setPerfumeImageUrls(productEntity.getProductImages().stream().map(ProductImage::getUrl).toList());
        recommendation.setPerfumeBrand(productEntity.getBrand());
        recommendation.setPerfumeGrade(productEntity.getGrade());
        recommendation.setReason(reason);
        recommendation.setSituation(situation);
        return recommendation;
    }
}
