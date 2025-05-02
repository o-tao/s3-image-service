package example.product.service;

import example.domain.images.Image;
import example.domain.images.ImageRepository;
import example.domain.products.Product;
import example.domain.products.ProductRepository;
import example.global.exception.CustomApplicationException;
import example.global.exception.ErrorCode;
import example.image.controller.dto.ImageResponse;
import example.product.controller.dto.ProductResponse;
import example.product.service.dto.ProductCreateInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    /**
     * [public 메서드]
     * - 상품 생성
     * - 이미지 목록이 존재하면 해당 이미지를 조회 후 productId가 할당되지 않은 이미지를 생성된 상품에 매핑
     * - 이미지의 경우 상품과 매핑된 이미지만 필터링하여 응답
     */
    @Transactional
    public ProductResponse<ImageResponse> createProduct(ProductCreateInfo productCreateInfo) {
        // [Step 1] 상품 저장
        Product product = productRepository.save(Product.create(
                productCreateInfo.getName(),
                productCreateInfo.getPrice(),
                productCreateInfo.getDescription()
        ));

        List<Image> images = Collections.emptyList();

        // [Step 2] 이미지 매핑 (이미지가 존재할 경우에만 처리)
        if (productCreateInfo.getImageIds() != null && !productCreateInfo.getImageIds().isEmpty()) { // 이미지 목록이 비어있지 않으면 처리
            images = findAllByImageId(productCreateInfo.getImageIds()); // 이미지 목록 조회
            images.stream()
                    .filter(image -> image.getProduct() == null) // 상품과 연결되지 않은 이미지만 필터링
                    .forEach(image -> image.assignProduct(product)); // 해당 이미지를 상품에 할당
        }

        // [Step 3] 응답 생성 (이미지는 생성되는 상품에 연결된 이미지만 포함)
        return ProductResponse.of(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                images.stream()
                        // 생성되는 상품에 매핑된 이미지만 필터링
                        .filter(image -> image.getProduct() != null && image.getProduct().getId().equals(product.getId()))
                        .map(image -> ImageResponse.of(
                                image.getId(),
                                image.getProduct().getId(),
                                image.getPath(),
                                image.getName(),
                                image.getCreatedAt(),
                                image.getUpdatedAt()
                        )).toList(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    /**
     * [public 메서드]
     * - 상품 상세조회 -> 상품 단일 정보, 이미지 list 응답
     */
    public ProductResponse<ImageResponse> detailProduct(Long productId) {
        Product product = findProductById(productId);
        List<Image> images = findImageByProductId(product.getId());

        return ProductResponse.of(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                images.stream()
                        .filter(image -> image.getProduct() != null && image.getProduct().getId().equals(product.getId()))
                        .map(image -> ImageResponse.of(
                                image.getId(),
                                image.getProduct().getId(),
                                image.getPath(),
                                image.getName(),
                                image.getCreatedAt(),
                                image.getUpdatedAt()
                        )).toList(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    /**
     * [private 메서드]
     * - 이미지 ID를 기준으로 이미지 목록을 조회
     * - 조회된 이미지가 없다면 예외 발생, List응답은 빈값으로 처리되어 Optional처리가 되지 않아 Empty로 직접 체크
     */
    private List<Image> findAllByImageId(List<Long> imageIds) {
        List<Image> images = imageRepository.findAllById(imageIds);
        if (images.isEmpty()) throw new CustomApplicationException(ErrorCode.IMAGE_ID_MISSING);
        return images;
    }

    /**
     * [private 메서드]
     * - productId 조회
     */
    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new CustomApplicationException(ErrorCode.NOT_FOUND_PRODUCT));
    }

    /**
     * [private 메서드]
     * - productId로 이미지 List 조회
     * - 이미지 없을 시 빈 리스트 응답
     */
    private List<Image> findImageByProductId(Long productId) {
        return imageRepository.findByProductId(productId);
    }
}
