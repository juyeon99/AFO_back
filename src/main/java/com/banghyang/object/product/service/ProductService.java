package com.banghyang.object.product.service;

import com.banghyang.common.mapper.Mapper;
import com.banghyang.common.type.NoteType;
import com.banghyang.common.util.ValidUtils;
import com.banghyang.object.note.entity.Note;
import com.banghyang.object.note.repository.NoteRepository;
import com.banghyang.object.product.dto.PerfumeResponse;
import com.banghyang.object.product.dto.ProductCreateRequest;
import com.banghyang.object.product.dto.ProductModifyRequest;
import com.banghyang.object.product.entity.Product;
import com.banghyang.object.product.entity.ProductImage;
import com.banghyang.object.product.repository.ProductImageRepository;
import com.banghyang.object.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final NoteRepository noteRepository;


    /**
     * @return 모든 향수 response 리스트(name 기준 오름차순 정렬)
     */
    @Cacheable(value = "perfumes") // 캐싱 사용
    public List<PerfumeResponse> getAllPerfumeResponses() {
        // perfume 엔티티 전체 가져와서 리스트에 담기
        List<Product> perfumeEntityList = productRepository.findByCategoryId(1L); // 향수 카테고리 아이디는 1(Long)
        return perfumeEntityList.stream().map(perfumeEntity -> {
            PerfumeResponse perfumeResponse = new PerfumeResponse();
            perfumeResponse.setId(perfumeEntity.getId());
            perfumeResponse.setNameEn(perfumeResponse.getNameEn());
            perfumeResponse.setNameKr(perfumeResponse.getNameKr());
            perfumeResponse.setBrand(perfumeEntity.getBrand());
            perfumeResponse.setGrade(perfumeEntity.getGrade());
            perfumeResponse.setContent(perfumeEntity.getContent());
            perfumeResponse.setSizeOption(perfumeEntity.getSizeOption());
            perfumeResponse.setMainAccord(perfumeEntity.getMainAccord());
            perfumeResponse.setIngredients(perfumeEntity.getIngredients());

            // 이미지 처리
            perfumeResponse.setImageUrls(
                    productImageRepository.findByProduct(perfumeEntity).stream()
                            .map(ProductImage::getUrl)
                            .toList()
            );

            // 싱글노트 처리

        })
    }

    /**
     * 새로운 향수 정보 생성 메소드(향수, 향수이미지, 노트)
     */
    @CacheEvict(value = "perfumes") // 수정 시 마다 캐시데이터 함께 업데이트
    public void createPerfume(ProductCreateRequest productCreateRequest) {
        // 새로운 향수 정보 담을 perfume 엔티티 생성
        // Mapper 클래스의 request -> entity 변환 메소드로 정보 담아줌
        Product newProductEntity = Mapper.mapPerfumeCreateRequestToEntity(productCreateRequest);
        // 정보 담은 엔티티 저장
        productRepository.save(newProductEntity);

        // 향수 이미지
        if (!productCreateRequest.getImageUrls().isEmpty()) {
            // 만약 request 에 이미지 url 정보가 담겨있다면 이미지 엔티티 생성 진행
            productCreateRequest.getImageUrls().forEach(imageUrl -> {
                ProductImage newProductImageEntity = ProductImage.builder()
                        .perfume(newProductEntity)
                        .url(imageUrl)
                        .build();
                productImageRepository.save(newProductImageEntity);
            });
        }

        // 노트
        if (!productCreateRequest.getSingleNote().isEmpty()) { // 싱글노트가 존재하고,
            if (productCreateRequest.getTopNote().isEmpty() &&
                    productCreateRequest.getMiddleNote().isEmpty() &&
                    productCreateRequest.getBaseNote().isEmpty()) { // 나머지 노트들이 다 null 일때
                // 새로운 조인 테이블 생성
                NoteSpice newSingleSpice = NoteSpice.builder().build();
                noteSpiceRepository.save(newSingleSpice);

                // 싱글노트 엔티티 생성
                Note newSingleNoteEntity = Note.builder()
                        .noteType(NoteType.SINGLE)
                        .perfume(newProductEntity)
                        .noteSpice(newSingleSpice)
                        .build();
                noteRepository.save(newSingleNoteEntity);

                productCreateRequest.getSingleNote().forEach(note -> {

                })

//                SingleNote newSingleNoteEntity = SingleNote.builder()
//                        .perfume(newPerfumeEntity)
//                        .spices(perfumeCreateRequest.getSingleNote())
//                        .build();
//                singleNoteRepository.save(newSingleNoteEntity); // 싱글노트 엔티티 생성하고 향수 엔티티와 연결
            } else {
                // 만약 싱글 노트와 다른 노트가 동시에 존재하면 예외 발생시키기
                throw new IllegalArgumentException("싱글 노트와 다른 종류의 노트가 동시에 존재할 수 없습니다.");
            }
//        } else { // 싱글노트가 존재하지 않는 경우 탑, 미들, 베이스 노트 생성 진행
            // 탑노트
//            if (ValidUtils.isNotBlank(perfumeCreateRequest.getTopNote())) {
//                TopNote newTopNoteEntity = TopNote.builder()
//                        .perfume(newPerfumeEntity)
//                        .spices(perfumeCreateRequest.getTopNote())
//                        .build();
//                topNoteRepository.save(newTopNoteEntity);
//            }

            // 미들노트
//            if (ValidUtils.isNotBlank(perfumeCreateRequest.getMiddleNote())) {
//                MiddleNote newMiddleNoteEntity = MiddleNote.builder()
//                        .perfume(newPerfumeEntity)
////                        .spices(perfumeCreateRequest.getMiddleNote())
//                        .build();
//                middleNoteRepository.save(newMiddleNoteEntity);
//            }

            // 베이스노트
//            if (ValidUtils.isNotBlank(perfumeCreateRequest.getBaseNote())) {
//                BaseNote newBaseNoteEntity = BaseNote.builder()
//                        .perfume(newPerfumeEntity)
////                        .spices(perfumeCreateRequest.getBaseNote())
//                        .build();
//                baseNoteRepository.save(newBaseNoteEntity);
//            }

//            if (!ValidUtils.isNotBlank(perfumeCreateRequest.getTopNote()) &&
//                    !ValidUtils.isNotBlank(perfumeCreateRequest.getMiddleNote()) &&
//                    !ValidUtils.isNotBlank(perfumeCreateRequest.getBaseNote())) {
//                // 노트가 아무것도 존재하지 않을 시 예외 발생시키기
//                throw new IllegalArgumentException("노트 정보가 존재하지 않아 향수 등록을 실패했습니다.");
//    }
        }
    }

    /**
     * 향수 정보 수정 메소드
     */
    @CacheEvict(value = "perfumes") // 수정 시 마다 캐시데이터 함께 업데이트
    public void modifyPerfume(ProductModifyRequest productModifyRequest) {
        // 수정할 perfume 엔티티 request 의 id 값으로 찾아오기
        Product targetProductEntity = productRepository.findById(productModifyRequest.getId())
                .orElseThrow(() -> new IllegalArgumentException("수정하려는 향수의 정보를 찾을 수 없습니다."));
        // 찾아온 perfume 엔티티 수정
        Product modifyProductEntity = Product.builder()
//                .name(perfumeModifyRequest.getName())
                .description(productModifyRequest.getDescription())
                .brand(productModifyRequest.getBrand())
                .grade(productModifyRequest.getGrade())
                .build();
        // 향수 엔티티 클래스에 만들어둔 수정 메소드로 수정 적용
        targetProductEntity.modify(modifyProductEntity);

        // 향수 이미지
        if (ValidUtils.isNotBlank(productModifyRequest.getImageUrl())) {
            // request 에 이미지 URL 존재할 시 이미지 수정 진행
//            PerfumeImage targetPerfumeImageEntity = targetPerfumeEntity.getPerfumeImage();
            // 수정 이미지 엔티티 생성
            ProductImage modifyProductImageEntity = ProductImage.builder()
                    .url(productModifyRequest.getImageUrl())
                    .perfume(targetProductEntity)
                    .build();

//            if (targetPerfumeImageEntity != null) {
//                // 기존 엔티티가 있다면 수정
//                targetPerfumeImageEntity.modify(modifyPerfumeImageEntity);
//            } else {
//                // 기존 엔티티가 없다면 생성
//                perfumeImageRepository.save(modifyPerfumeImageEntity);
//            }
//        } else {
//            if (targetPerfumeEntity.getPerfumeImage() != null) {
//                perfumeImageRepository.delete(targetPerfumeEntity.getPerfumeImage());
//            }
        }

        // 노트
//        if (ValidUtils.isNotBlank(perfumeModifyRequest.getSingleNote())) {
//            if (!ValidUtils.isNotBlank(perfumeModifyRequest.getTopNote()) &&
//                    !ValidUtils.isNotBlank(perfumeModifyRequest.getMiddleNote()) &&
//                    !ValidUtils.isNotBlank(perfumeModifyRequest.getBaseNote())) {
        // request 에 싱글노트가 존재하고 다른 노트가 없을 시 싱글노트 수정 진행
//                SingleNote modifySingleNoteEntity = SingleNote.builder()
//                        .perfume(targetPerfumeEntity)
//                        .spices(perfumeModifyRequest.getSingleNote())
//                        .build();

//                if (targetPerfumeEntity.getSingleNote() != null) {
//                    targetPerfumeEntity.getSingleNote().modify(modifySingleNoteEntity);
//                } else {
//                    singleNoteRepository.save(modifySingleNoteEntity);
//                }

        // 싱글노트 외의 다른 노트 엔티티 모두 삭제
        // 탑노트 삭제
//                if (targetPerfumeEntity.getTopNote() != null) {
//                    topNoteRepository.delete(targetPerfumeEntity.getTopNote());
//                }
//                // 미들노트 삭제
//                if (targetPerfumeEntity.getMiddleNote() != null) {
//                    middleNoteRepository.delete(targetPerfumeEntity.getMiddleNote());
//                }
//                // 베이스노트 삭제
//                if (targetPerfumeEntity.getBaseNote() != null) {
//                    baseNoteRepository.delete(targetPerfumeEntity.getBaseNote());
//                }
//            } else {
        // request 에 싱글노트와 다른 노트가 함께 존재시 예외 발생시키기
//                throw new IllegalArgumentException("싱글 노트와 다른 종류의 노트가 동시에 존재할 수 없습니다.");
//            }
//        } else { // 싱글노트가 존재하지 않을 시 싱글 노트 엔티티 삭제하고 탑, 미들, 베이스 노트 수정 진행
        // 탑노트
//            if (ValidUtils.isNotBlank(perfumeModifyRequest.getTopNote())) {
        // 싱글노트 존재 시 삭제하기
//                if (targetPerfumeEntity.getSingleNote() != null) {
//                    singleNoteRepository.delete(targetPerfumeEntity.getSingleNote());
//                }

//                TopNote modifyTopNoteEntity = TopNote.builder()
//                        .perfume(targetPerfumeEntity)
//                        .spices(perfumeModifyRequest.getTopNote())
//                        .build();

//                if (targetPerfumeEntity.getTopNote() != null) {
//                    targetPerfumeEntity.getTopNote().modify(modifyTopNoteEntity);
//                } else {
//                    topNoteRepository.save(modifyTopNoteEntity);
//                }
//            } else {
        // 탑노트 입력 정보가 없다면 기존 탑노트 존재 검증 후 삭제 처리
//                if (targetPerfumeEntity.getTopNote() != null) {
//                    topNoteRepository.delete(targetPerfumeEntity.getTopNote());
//                }
//            }
        // 미들노트
//            if (ValidUtils.isNotBlank(perfumeModifyRequest.getMiddleNote())) {
        // 싱글노트 존재 시 삭제하기
//                if (targetPerfumeEntity.getSingleNote() != null) {
//                    singleNoteRepository.delete(targetPerfumeEntity.getSingleNote());
//                }

//                MiddleNote modifyMiddleNoteEntity = MiddleNote.builder()
//                        .perfume(targetPerfumeEntity)
//                        .spices(perfumeModifyRequest.getMiddleNote())
//                        .build();

//                if (targetPerfumeEntity.getMiddleNote() != null) {
//                    targetPerfumeEntity.getMiddleNote().modify(modifyMiddleNoteEntity);
//                } else {
//                    middleNoteRepository.save(modifyMiddleNoteEntity);
//                }
//            } else {
//                // 미들노트 입력 정보가 없다면 기존 미들노트 존재 검증 후 삭제 처리
//                if (targetPerfumeEntity.getMiddleNote() != null) {
//                    middleNoteRepository.delete(targetPerfumeEntity.getMiddleNote());
//                }
//            }
        // 베이스노트
//            if (ValidUtils.isNotBlank(perfumeModifyRequest.getBaseNote())) {
        // 싱글노트 존재 시 삭제하기
//                if (targetPerfumeEntity.getSingleNote() != null) {
//                    singleNoteRepository.delete(targetPerfumeEntity.getSingleNote());
//                }

//                BaseNote modifyBaseNoteEntity = BaseNote.builder()
//                        .perfume(targetPerfumeEntity)
//                        .spices(perfumeModifyRequest.getBaseNote())
//                        .build();

//                if (targetPerfumeEntity.getBaseNote() != null) {
//                    targetPerfumeEntity.getBaseNote().modify(modifyBaseNoteEntity);
//                } else {
//                    baseNoteRepository.save(modifyBaseNoteEntity);
//                }
//            } else {
//                // 베이스노트 입력 정보가 없다면 기존 베이스노트 존재 검증 후 삭제 처리
//                if (targetPerfumeEntity.getBaseNote() != null) {
//                    baseNoteRepository.delete(targetPerfumeEntity.getBaseNote());
//                }
//            }
//        }
    }

    /**
     * 향수 삭제 메소드
     */
    @CacheEvict(value = "perfumes") // 수정 시 마다 캐시데이터 함께 업데이트
    public void deletePerfume(Long perfumeId) {
        Product targetProductEntity = productRepository.findById(perfumeId)
                .orElseThrow(() -> new IllegalArgumentException("삭제하려는 향수의 정보를 찾을 수 업습니다."));
        productRepository.delete(targetProductEntity);
    }
}
