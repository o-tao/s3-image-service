package example.product.service;

import example.domain.images.Image;
import example.domain.products.Product;
import example.domain.products.ProductRepository;
import example.global.exception.CustomApplicationException;
import example.global.exception.ErrorCode;
import example.image.controller.dto.ImageResponse;
import example.image.service.ImageService;
import example.product.controller.dto.ProductResponse;
import example.product.service.dto.ProductCreateInfo;
import example.product.service.dto.ProductUpdateInfo;
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
    private final ImageService imageService;

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
            images = imageService.findAllByImageId(productCreateInfo.getImageIds()); // 이미지 목록 조회
            images.stream()
                    .filter(image -> image.getProduct() == null) // 상품과 연결되지 않은 이미지만 필터링
                    .forEach(image -> image.assignProduct(product)); // 해당 이미지를 상품에 할당
        }

        // [Step 3] 응답 생성 (이미지는 생성되는 상품에 연결된 이미지만 포함)
        return ProductResponse.of(
                product,
                images.stream().map(ImageResponse::of).toList()
        );
    }

    /**
     * [public 메서드]
     * - 상품 상세조회 -> 상품 단일 정보, 이미지 list 응답
     */
    public ProductResponse<ImageResponse> detailProduct(Long productId) {
        Product product = findProductById(productId);
        List<Image> images = imageService.findImageByProductId(product.getId());

        return ProductResponse.of(
                product,
                images.stream().map(ImageResponse::of).toList()
        );
    }

    /**
     * [public 메서드]
     * - 상품 수정
     * - 이미지 매핑 null 벌크 업데이트 후 요청 데이터에 imageIds 존재 시 재 매핑 (벌크업데이트)
     */
    @Transactional
    public ProductResponse<ImageResponse> updateProduct(ProductUpdateInfo productUpdateInfo) {
        // [Step 1] 업데이트 할 product 조회
        Product product = findProductById(productUpdateInfo.getId());

        // [Step 2] 상품 정보 업데이트
        product.update(
                productUpdateInfo.getName(),
                productUpdateInfo.getPrice(),
                productUpdateInfo.getDescription()
        );

        // [Step 3] 기존 이미지 매핑 해제 (기존의 product와 연결된 이미지들의 product를 null로 설정)
        imageService.clearProductFromImages(productUpdateInfo.getId());

        // [Step 4] 이미지 ID가 존재하면, 해당 이미지 ID로 새로운 이미지 매핑 (벌크 업데이트)
        if (!productUpdateInfo.getImageIds().isEmpty()) {
            imageService.assignProduct(productUpdateInfo.getId(), productUpdateInfo.getImageIds());
        }

        // [Step 5] 응답 생성 (상품에 매핑된 이미지 목록을 포함)
        List<Image> images = imageService.findImageByProductId(product.getId());

        return ProductResponse.of(
                product,
                images.stream().map(ImageResponse::of).toList()
        );
    }

    /**
     * [private 메서드]
     * - productId 조회
     */
    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new CustomApplicationException(ErrorCode.NOT_FOUND_PRODUCT));
    }
}
