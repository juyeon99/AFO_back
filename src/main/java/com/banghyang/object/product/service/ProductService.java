package com.banghyang.object.product.service;

import com.banghyang.common.type.NoteType;
import com.banghyang.object.note.entity.Note;
import com.banghyang.object.note.repository.NoteRepository;
import com.banghyang.object.product.dto.*;
import com.banghyang.object.product.entity.Product;
import com.banghyang.object.product.entity.ProductImage;
import com.banghyang.object.product.repository.ProductImageRepository;
import com.banghyang.object.product.repository.ProductRepository;
import com.banghyang.object.spice.entity.Spice;
import com.banghyang.object.spice.repository.SpiceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final NoteRepository noteRepository;
    private final SpiceRepository spiceRepository;
    private final WebClient webClient;

    /**
     * @return 모든 향수 response 리스트(name 기준 오름차순 정렬)
     */
    @Cacheable(value = "products") // 캐싱 사용
    public List<PerfumeResponse> getAllPerfumeResponses() {
        // perfume 엔티티 전체 가져와서 리스트에 담기
        List<Product> perfumeEntityList = productRepository.findByCategoryId(1L); // 향수 카테고리 아이디는 1(Long)

        // 향수 엔티티 리스트에 stream 으로 항목마다 접근하여 response 로 변환하는 작업 거치기
        return perfumeEntityList.stream().map(perfumeEntity -> {
                    PerfumeResponse perfumeResponse = new PerfumeResponse();
                    perfumeResponse.setId(perfumeEntity.getId()); // 아이디
                    perfumeResponse.setNameEn(perfumeEntity.getNameEn()); // 영문명
                    perfumeResponse.setNameKr(perfumeEntity.getNameKr()); // 한글명
                    perfumeResponse.setBrand(perfumeEntity.getBrand()); // 브랜드
                    perfumeResponse.setGrade(perfumeEntity.getGrade()); // 부향률
                    perfumeResponse.setContent(perfumeEntity.getContent()); // 설명
                    perfumeResponse.setSizeOption(perfumeEntity.getSizeOption()); // 용량 옵션
                    perfumeResponse.setMainAccord(perfumeEntity.getMainAccord()); // 메인어코드
                    perfumeResponse.setIngredients(perfumeEntity.getIngredients()); // 성분

                    // 이미지
                    perfumeResponse.setImageUrlList(
                            productImageRepository.findByProduct(perfumeEntity).stream()
                                    .map(ProductImage::getUrl)
                                    .toList()
                    );

                    // 노트
                    List<Note> noteEntityList = noteRepository.findByProduct(perfumeEntity); // 모든 노트 조회
                    // 싱글노트
                    perfumeResponse.setSingleNote(
                            noteEntityList.stream()
                                    .filter(noteEntity -> noteEntity.getNoteType().equals(NoteType.SINGLE))
                                    .map(noteEntity -> noteEntity.getSpice().getNameKr())
                                    .collect(Collectors.joining(", "))
                    );
                    // 탑노트
                    perfumeResponse.setTopNote(
                            noteEntityList.stream()
                                    .filter(noteEntity -> noteEntity.getNoteType().equals(NoteType.TOP))
                                    .map(noteEntity -> noteEntity.getSpice().getNameKr())
                                    .collect(Collectors.joining(", "))
                    );
                    // 미들노트
                    perfumeResponse.setMiddleNote(
                            noteEntityList.stream()
                                    .filter(noteEntity -> noteEntity.getNoteType().equals(NoteType.MIDDLE))
                                    .map(noteEntity -> noteEntity.getSpice().getNameKr())
                                    .collect(Collectors.joining(", "))
                    );
                    // 베이스노트
                    perfumeResponse.setBaseNote(
                            noteEntityList.stream()
                                    .filter(noteEntity -> noteEntity.getNoteType().equals(NoteType.BASE))
                                    .map(noteEntity -> noteEntity.getSpice().getNameKr())
                                    .collect(Collectors.joining(", "))
                    );
                    return perfumeResponse;
                })
                .sorted(Comparator.comparing(PerfumeResponse::getNameKr)) // 한글명순으로 정렬
                .toList();
    }

    /**
     * 새로운 제품 정보 등록 메소드
     */
    @CacheEvict(value = "products") // 수정 시 마다 캐시데이터 함께 업데이트
    public void createProduct(ProductCreateRequest productCreateRequest) {
        // 제품 정보 등록
        Product newProductEntity = Product.builder()
                .nameEn(productCreateRequest.getNameEn())
                .nameKr(productCreateRequest.getNameKr())
                .brand(productCreateRequest.getBrand())
                .grade(productCreateRequest.getGrade())
                .content(productCreateRequest.getContent())
                .sizeOption(productCreateRequest.getSizeOption())
                .mainAccord(productCreateRequest.getMainAccord())
                .ingredients(productCreateRequest.getIngredients())
                .build();
        productRepository.save(newProductEntity); // 새로운 제품 정보 저장

        // 이미지
        if (!productCreateRequest.getImageUrlList().isEmpty()) {
            // 만약 request 에 이미지 url 정보가 담겨있다면 이미지 엔티티 생성 진행
            productCreateRequest.getImageUrlList().forEach(imageUrl -> {
                ProductImage newProductImageEntity = ProductImage.builder()
                        .product(newProductEntity)
                        .url(imageUrl)
                        .build();
                productImageRepository.save(newProductImageEntity);
            });
        }

        // 노트
        if (!productCreateRequest.getSingleNoteList().isEmpty() &&
                productCreateRequest.getTopNoteList().isEmpty() &&
                productCreateRequest.getMiddleNoteList().isEmpty() &&
                productCreateRequest.getBaseNoteList().isEmpty()) {
            // request 에 싱글노트만 존재하고 나머지 노트는 존재하지 않을 때
            productCreateRequest.getSingleNoteList().forEach(spiceNameKr -> {
                // 향료명으로 향료 엔티티 찾아오기
                Spice targetSpice = spiceRepository.findByNameKr(spiceNameKr);
                if (targetSpice != null) {
                    // 향료 정보가 존재할 때는 바로 노트 생성 후 저장
                    Note newNoteEntity = Note.builder()
                            .product(newProductEntity)
                            .spice(targetSpice)
                            .noteType(NoteType.SINGLE)
                            .build();
                    noteRepository.save(newNoteEntity);
                } else {
                    // 향료 임시 등록
                    Spice newSpiceEntity = Spice.builder()
                            .nameKr(spiceNameKr)
                            .build();
                    spiceRepository.save(newSpiceEntity);
                    // 임시 생성한 향료 정보로 노트까지 생성
                    Note newNoteEntity = Note.builder()
                            .product(newProductEntity)
                            .spice(newSpiceEntity)
                            .noteType(NoteType.SINGLE)
                            .build();
                    noteRepository.save(newNoteEntity);
                }
            });
        } else {
            throw new IllegalArgumentException("[제품-서비스-생성]싱글 타입과 다른 타입의 노트가 함께 존재할 수 없습니다.");
        }
        // 나머지 노트 처리
        if (!productCreateRequest.getTopNoteList().isEmpty()) {
            // 탑노트
            productCreateRequest.getTopNoteList().forEach(spiceNameKr -> {
                Spice targetSpice = spiceRepository.findByNameKr(spiceNameKr);
                if (targetSpice != null) {
                    // 향료 정보가 존재할 때는 바로 노트 생성 후 저장
                    Note newNoteEntity = Note.builder()
                            .product(newProductEntity)
                            .spice(targetSpice)
                            .noteType(NoteType.TOP)
                            .build();
                    noteRepository.save(newNoteEntity);
                } else {
                    // 향료 임시 등록
                    Spice newSpiceEntity = Spice.builder()
                            .nameKr(spiceNameKr)
                            .build();
                    spiceRepository.save(newSpiceEntity);
                    // 임시 생성한 향료 정보로 노트까지 생성
                    Note newNoteEntity = Note.builder()
                            .product(newProductEntity)
                            .spice(newSpiceEntity)
                            .noteType(NoteType.TOP)
                            .build();
                    noteRepository.save(newNoteEntity);
                }
            });
        } else if (!productCreateRequest.getMiddleNoteList().isEmpty()) {
            // 미들노트
            productCreateRequest.getMiddleNoteList().forEach(spiceNameKr -> {
                Spice targetSpice = spiceRepository.findByNameKr(spiceNameKr);
                if (targetSpice != null) {
                    // 향료 정보가 존재할 때는 바로 노트 생성 후 저장
                    Note newNoteEntity = Note.builder()
                            .product(newProductEntity)
                            .spice(targetSpice)
                            .noteType(NoteType.MIDDLE)
                            .build();
                    noteRepository.save(newNoteEntity);
                } else {
                    // 향료 임시 등록
                    Spice newSpiceEntity = Spice.builder()
                            .nameKr(spiceNameKr)
                            .build();
                    spiceRepository.save(newSpiceEntity);
                    // 임시 생성한 향료 정보로 노트까지 생성
                    Note newNoteEntity = Note.builder()
                            .product(newProductEntity)
                            .spice(newSpiceEntity)
                            .noteType(NoteType.MIDDLE)
                            .build();
                    noteRepository.save(newNoteEntity);
                }
            });
        } else if (!productCreateRequest.getBaseNoteList().isEmpty()) {
            // 베이스노트
            productCreateRequest.getBaseNoteList().forEach(spiceNameKr -> {
                Spice targetSpice = spiceRepository.findByNameKr(spiceNameKr);
                if (targetSpice != null) {
                    // 향료 정보가 존재할 때는 바로 노트 생성 후 저장
                    Note newNoteEntity = Note.builder()
                            .product(newProductEntity)
                            .spice(targetSpice)
                            .noteType(NoteType.BASE)
                            .build();
                    noteRepository.save(newNoteEntity);
                } else {
                    // 향료 임시 등록
                    Spice newSpiceEntity = Spice.builder()
                            .nameKr(spiceNameKr)
                            .build();
                    spiceRepository.save(newSpiceEntity);
                    // 임시 생성한 향료 정보로 노트까지 생성
                    Note newNoteEntity = Note.builder()
                            .product(newProductEntity)
                            .spice(newSpiceEntity)
                            .noteType(NoteType.BASE)
                            .build();
                    noteRepository.save(newNoteEntity);
                }
            });
        }
    }

    /**
     * 제품 정보 수정 메소드
     */
    @CacheEvict(value = "products") // 수정 시 마다 캐시데이터 함께 업데이트
    public void modifyProduct(ProductModifyRequest productModifyRequest) {
        // 수정할 제품 엔티티 request 의 id 값으로 찾아오기
        Product targetProductEntity = productRepository.findById(productModifyRequest.getId()).orElseThrow(() ->
                new EntityNotFoundException("[제품-서비스-수정]아이디에 해당하는 제품 엔티티를 찾을 수 없습니다."));
        // request 정보 담기
        Product modifyProductEntity = Product.builder()
                .nameEn(productModifyRequest.getNameEn())
                .nameKr(productModifyRequest.getNameKr())
                .brand(productModifyRequest.getBrand())
                .grade(productModifyRequest.getGrade())
                .content(productModifyRequest.getContent())
                .sizeOption(productModifyRequest.getSizeOption())
                .mainAccord(productModifyRequest.getMainAccord())
                .ingredients(productModifyRequest.getIngredients())
                .build();
        // 엔티티 수정 적용
        targetProductEntity.modify(modifyProductEntity);

        // 이미지 처리
        // 제품에 해당하는 모든 이미지 조회
        List<ProductImage> targetProductImageEntityList = productImageRepository.findByProduct(targetProductEntity);
        // 이미지 URL 만 뽑아서 Set 으로 만들기
        Set<String> targetProductImageUrlSet = targetProductImageEntityList.stream()
                .map(ProductImage::getUrl)
                .collect(Collectors.toSet());
        // 기존에 존재하지만 request 에는 없는 이미지 골라내서 삭제하기
        List<ProductImage> productImagesToDelete = targetProductImageEntityList.stream()
                .filter(productImageEntity ->
                        !productModifyRequest.getImageUrlSet().contains(productImageEntity.getUrl()))
                .toList();
        productImageRepository.deleteAll(productImagesToDelete);
        // 기존에 존재하지 않지만 request 에는 존재하는 URL 생성하기
        List<ProductImage> productImagesToAdd = productModifyRequest.getImageUrlSet().stream()
                .filter(url -> !targetProductImageUrlSet.contains(url))
                .map(url -> ProductImage.builder()
                        .product(targetProductEntity)
                        .url(url)
                        .build())
                .toList();
        productImageRepository.saveAll(productImagesToAdd);
        // 기존에 존재하고 request 에도 존재하는 이미지는 별도의 처리없이 그대로 유지

        // 전체 노트 찾아오기
        List<Note> targetNoteEntityList = noteRepository.findByProduct(targetProductEntity);
        // 싱글노트
        if (!productModifyRequest.getSingleNoteSet().isEmpty() &&
                productModifyRequest.getTopNoteSet().isEmpty() &&
                productModifyRequest.getMiddleNoteSet().isEmpty() &&
                productModifyRequest.getBaseNoteSet().isEmpty()) {
            // request 에 싱글노트만 있고 나머지 노트는 없을때만 싱글노트 처리 진행
            // 기존의 싱글노트 엔티티들 이름만 가져와서 Set 으로 만들기
            Set<String> targetSingleSpiceNameKrSet = targetNoteEntityList.stream()
                    .filter(noteEntity -> noteEntity.getNoteType().equals(NoteType.SINGLE))
                    .map(noteEntity -> noteEntity.getSpice().getNameKr())
                    .collect(Collectors.toSet());
            // 기존엔 존재하지만 request 에 없는 노트 리스트
            List<Note> singleNotesToDelete = targetNoteEntityList.stream()
                    .filter(noteEntity -> noteEntity.getNoteType().equals(NoteType.SINGLE))
                    .filter(noteEntity ->
                            !productModifyRequest.getSingleNoteSet().contains(noteEntity.getSpice().getNameKr()))
                    .toList();
            // 싱글타입이 아닌 노트 리스트
            List<Note> otherTypeNotesList = targetNoteEntityList.stream()
                    .filter(noteEntity -> !noteEntity.getNoteType().equals(NoteType.SINGLE))
                    .toList();
            // 최종적으로 삭제할 노트 엔티티 리스트
            List<Note> notesToDelete = new ArrayList<>();
            notesToDelete.addAll(singleNotesToDelete); // 리퀘스트에 없는 싱글노트 담기
            notesToDelete.addAll(otherTypeNotesList); // 다른 타입의 노트들 담기
            noteRepository.deleteAll(notesToDelete); // 모두 삭제 처리
            // 기존엔 없지만 request 엔 있는 노트 정보 리스트
            productModifyRequest.getSingleNoteSet().stream()
                    .filter(spiceNameKr -> !targetSingleSpiceNameKrSet.contains(spiceNameKr))
                    .forEach(spiceNameKr -> {
                        Spice targetSpice = spiceRepository.findByNameKr(spiceNameKr);
                        if (targetSpice != null) {
                            // 향료 정보가 존재할 때는 바로 노트 생성 후 저장
                            Note newNoteEntity = Note.builder()
                                    .product(targetProductEntity)
                                    .spice(targetSpice)
                                    .noteType(NoteType.SINGLE)
                                    .build();
                            noteRepository.save(newNoteEntity);
                        } else {
                            // 향료 임시 등록
                            Spice newSpiceEntity = Spice.builder()
                                    .nameKr(spiceNameKr)
                                    .build();
                            spiceRepository.save(newSpiceEntity);
                            // 임시 생성한 향료 정보로 노트까지 생성
                            Note newNoteEntity = Note.builder()
                                    .product(targetProductEntity)
                                    .spice(newSpiceEntity)
                                    .noteType(NoteType.SINGLE)
                                    .build();
                            noteRepository.save(newNoteEntity);
                        }
                    });
        } else {
            throw new IllegalArgumentException("[제품-서비스-수정]싱글 타입과 다른 타입의 노트가 동시에 존재할 수 없습니다.");
        }
        // 나머지 노트 처리
        // 탑노트
        if (!productModifyRequest.getTopNoteSet().isEmpty()) {
            // 기존의 탑노트 엔티티들 이름만 가져와서 Set 만들기
            Set<String> targetTopSpiceNameKrSet = targetNoteEntityList.stream()
                    .filter(noteEntity -> noteEntity.getNoteType().equals(NoteType.TOP))
                    .map(noteEntity -> noteEntity.getSpice().getNameKr())
                    .collect(Collectors.toSet());
            // 기존엔 존재하지만 request 에 없는 노트 리스트
            List<Note> topNotesToDelete = targetNoteEntityList.stream()
                    .filter(noteEntity -> noteEntity.getNoteType().equals(NoteType.TOP))
                    .filter(noteEntity ->
                            !productModifyRequest.getTopNoteSet().contains(noteEntity.getSpice().getNameKr()))
                    .toList();
            // 기존의 싱글 노트 엔티티 리스트
            List<Note> singleNotesToDelete = targetNoteEntityList.stream()
                    .filter(noteEntity -> noteEntity.getNoteType().equals(NoteType.SINGLE))
                    .toList();
            if (singleNotesToDelete.isEmpty()) {
                // 기존 싱글노트가 없다면 삭제할 탑노트만 처리
                noteRepository.deleteAll(topNotesToDelete);
            } else {
                // 기존 싱글노트가 존재한다면 함꼐 삭제 처리
                List<Note> notesToDelete = new ArrayList<>();
                notesToDelete.addAll(topNotesToDelete);
                notesToDelete.addAll(singleNotesToDelete);
                noteRepository.deleteAll(notesToDelete);
            }
            // 리퀘스트에는 있지만 기존엔 없는 향료 리스트(추가해야할 노트)
            productModifyRequest.getTopNoteSet().stream()
                    .filter(spiceNameKr -> !targetTopSpiceNameKrSet.contains(spiceNameKr))
                    .forEach(spiceNameKr -> {
                        Spice targetSpice = spiceRepository.findByNameKr(spiceNameKr);
                        if (targetSpice != null) {
                            // 향료 정보가 존재할 때는 바로 노트 생성 후 저장
                            Note newNoteEntity = Note.builder()
                                    .product(targetProductEntity)
                                    .spice(targetSpice)
                                    .noteType(NoteType.TOP)
                                    .build();
                            noteRepository.save(newNoteEntity);
                        } else {
                            // 향료 임시 등록
                            Spice newSpiceEntity = Spice.builder()
                                    .nameKr(spiceNameKr)
                                    .build();
                            spiceRepository.save(newSpiceEntity);
                            // 임시 생성한 향료 정보로 노트까지 생성
                            Note newNoteEntity = Note.builder()
                                    .product(targetProductEntity)
                                    .spice(newSpiceEntity)
                                    .noteType(NoteType.TOP)
                                    .build();
                            noteRepository.save(newNoteEntity);
                        }
                    });
        }
        // 미들노트
        if (!productModifyRequest.getMiddleNoteSet().isEmpty()) {
            // 기존의 미들노트 엔티티들 이름만 가져와서 Set 만들기
            Set<String> targetMiddleSpiceNameKrSet = targetNoteEntityList.stream()
                    .filter(noteEntity -> noteEntity.getNoteType().equals(NoteType.MIDDLE))
                    .map(noteEntity -> noteEntity.getSpice().getNameKr())
                    .collect(Collectors.toSet());
            // 기존엔 존재하지만 request 에 없는 노트 리스트
            List<Note> middleNotesToDelete = targetNoteEntityList.stream()
                    .filter(noteEntity -> noteEntity.getNoteType().equals(NoteType.MIDDLE))
                    .filter(noteEntity ->
                            !productModifyRequest.getTopNoteSet().contains(noteEntity.getSpice().getNameKr()))
                    .toList();
            // 기존의 싱글 노트 엔티티 리스트
            List<Note> singleNotesToDelete = targetNoteEntityList.stream()
                    .filter(noteEntity -> noteEntity.getNoteType().equals(NoteType.SINGLE))
                    .toList();
            if (singleNotesToDelete.isEmpty()) {
                // 기존 싱글노트가 없다면 삭제할 미들노트만 처리
                noteRepository.deleteAll(middleNotesToDelete);
            } else {
                // 기존 싱글노트가 존재한다면 함꼐 삭제 처리
                List<Note> notesToDelete = new ArrayList<>();
                notesToDelete.addAll(middleNotesToDelete);
                notesToDelete.addAll(singleNotesToDelete);
                noteRepository.deleteAll(notesToDelete);
            }
            // 리퀘스트에는 있지만 기존엔 없는 향료 리스트(추가해야할 노트)
            productModifyRequest.getTopNoteSet().stream()
                    .filter(spiceNameKr -> !targetMiddleSpiceNameKrSet.contains(spiceNameKr))
                    .forEach(spiceNameKr -> {
                        Spice targetSpice = spiceRepository.findByNameKr(spiceNameKr);
                        if (targetSpice != null) {
                            // 향료 정보가 존재할 때는 바로 노트 생성 후 저장
                            Note newNoteEntity = Note.builder()
                                    .product(targetProductEntity)
                                    .spice(targetSpice)
                                    .noteType(NoteType.MIDDLE)
                                    .build();
                            noteRepository.save(newNoteEntity);
                        } else {
                            // 향료 임시 등록
                            Spice newSpiceEntity = Spice.builder()
                                    .nameKr(spiceNameKr)
                                    .build();
                            spiceRepository.save(newSpiceEntity);
                            // 임시 생성한 향료 정보로 노트까지 생성
                            Note newNoteEntity = Note.builder()
                                    .product(targetProductEntity)
                                    .spice(newSpiceEntity)
                                    .noteType(NoteType.MIDDLE)
                                    .build();
                            noteRepository.save(newNoteEntity);
                        }
                    });
        }
        // 베이스노트
        if (!productModifyRequest.getBaseNoteSet().isEmpty()) {
            // 기존의 탑노트 엔티티들 이름만 가져와서 Set 만들기
            Set<String> targetBaseSpiceNameKrSet = targetNoteEntityList.stream()
                    .filter(noteEntity -> noteEntity.getNoteType().equals(NoteType.BASE))
                    .map(noteEntity -> noteEntity.getSpice().getNameKr())
                    .collect(Collectors.toSet());
            // 기존엔 존재하지만 request 에 없는 노트 리스트
            List<Note> baseNotesToDelete = targetNoteEntityList.stream()
                    .filter(noteEntity -> noteEntity.getNoteType().equals(NoteType.BASE))
                    .filter(noteEntity ->
                            !productModifyRequest.getTopNoteSet().contains(noteEntity.getSpice().getNameKr()))
                    .toList();
            // 기존의 싱글 노트 엔티티 리스트
            List<Note> singleNotesToDelete = targetNoteEntityList.stream()
                    .filter(noteEntity -> noteEntity.getNoteType().equals(NoteType.SINGLE))
                    .toList();
            if (singleNotesToDelete.isEmpty()) {
                // 기존 싱글노트가 없다면 삭제할 베이스노트만 처리
                noteRepository.deleteAll(baseNotesToDelete);
            } else {
                // 기존 싱글노트가 존재한다면 함꼐 삭제 처리
                List<Note> notesToDelete = new ArrayList<>();
                notesToDelete.addAll(baseNotesToDelete);
                notesToDelete.addAll(singleNotesToDelete);
                noteRepository.deleteAll(notesToDelete);
            }
            // 리퀘스트에는 있지만 기존엔 없는 향료 리스트(추가해야할 노트)
            productModifyRequest.getTopNoteSet().stream()
                    .filter(spiceNameKr -> !targetBaseSpiceNameKrSet.contains(spiceNameKr))
                    .forEach(spiceNameKr -> {
                        Spice targetSpice = spiceRepository.findByNameKr(spiceNameKr);
                        if (targetSpice != null) {
                            // 향료 정보가 존재할 때는 바로 노트 생성 후 저장
                            Note newNoteEntity = Note.builder()
                                    .product(targetProductEntity)
                                    .spice(targetSpice)
                                    .noteType(NoteType.BASE)
                                    .build();
                            noteRepository.save(newNoteEntity);
                        } else {
                            // 향료 임시 등록
                            Spice newSpiceEntity = Spice.builder()
                                    .nameKr(spiceNameKr)
                                    .build();
                            spiceRepository.save(newSpiceEntity);
                            // 임시 생성한 향료 정보로 노트까지 생성
                            Note newNoteEntity = Note.builder()
                                    .product(targetProductEntity)
                                    .spice(newSpiceEntity)
                                    .noteType(NoteType.BASE)
                                    .build();
                            noteRepository.save(newNoteEntity);
                        }
                    });
        }
    }

    /**
     * 제품 삭제 메소드
     */
    @CacheEvict(value = "products") // 수정 시 마다 캐시데이터 함께 업데이트
    public void deletePerfume(Long perfumeId) {
        // 삭제할 제품 엔티티
        Product targetProductEntity = productRepository.findById(perfumeId).orElseThrow(() ->
                new EntityNotFoundException("[제품-서비스-삭제]삭제하려는 제품의 정보를 찾을 수 업습니다."));
        // 삭제할 제품 이미지들
        List<ProductImage> imagesToDelete = productImageRepository.findByProduct(targetProductEntity);
        // 삭제할 노트들
        List<Note> notesToDelete = noteRepository.findByProduct(targetProductEntity);

        productImageRepository.deleteAll(imagesToDelete);
        noteRepository.deleteAll(notesToDelete);
        productRepository.delete(targetProductEntity);
    }

    public UserResponse recommendDiffusers(UserRequest request) {
        try {
            // FastAPI 서버에서 추천 받기
            log.info("@@@@@@@@@@@@@@@@추천 받기 전");

            DiffuserResponse diffuserResponse = webClient
                    .post()
                    .uri("http://localhost:8000/diffuser/recommend")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(DiffuserResponse.class)
                    .block();

            log.info("FastAPI response: {}", diffuserResponse);  // 응답 내용 확인

            UserResponse userResponse = new UserResponse();

            if (diffuserResponse != null) {
                // 추천 정보와 사용 방법 설정
                userResponse.setRecommendations(diffuserResponse.getRecommendations());
                userResponse.setUsageRoutine(diffuserResponse.getUsageRoutine());

                // 첫 번째 추천 제품의 이미지 URL 설정
                if (diffuserResponse.getRecommendations() != null && !diffuserResponse.getRecommendations().isEmpty()) {
                    Product product = productRepository.findById(diffuserResponse.getRecommendations().get(0).getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found with id: " +
                                    diffuserResponse.getRecommendations().get(0).getProductId()));

                    userResponse.setImageUrl(
                            productImageRepository.findByProduct(product).stream()
                                    .map(ProductImage::getUrl)
                                    .toList()
                                    .get(0)
                    );
                }
            }
            return userResponse;
        } catch (Exception e) {
            throw new RuntimeException("디퓨저 추천 처리 중 오류 발생: " + e.getMessage());
        }
    }
}
