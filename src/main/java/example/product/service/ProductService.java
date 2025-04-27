package example.product.service;

import example.domain.images.Image;
import example.domain.images.ImageRepository;
import example.domain.products.Product;
import example.domain.products.ProductRepository;
import example.global.exception.CustomApplicationException;
import example.global.exception.ErrorCode;
import example.product.service.dto.ProductCreateInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    /**
     * [public 메서드]
     * - 상품 생성
     * - 이미지 목록이 존재하면 해당 이미지를 상품에 할당
     */
    @Transactional
    public void createProduct(ProductCreateInfo productCreateInfo) {
        // [Step 1] 상품 저장
        Product product = productRepository.save(Product.create(
                productCreateInfo.getName(),
                productCreateInfo.getPrice(),
                productCreateInfo.getDescription()
        ));

        // [Step 2] 이미지 매핑 (이미지가 존재할 경우에만 처리)
        Optional.ofNullable(productCreateInfo.getImageNames())
                .filter(names -> !names.isEmpty()) // 이미지 이름 목록이 비어있지 않은 경우에만 처리
                .map(this::findByImageNames) // 이미지 목록 조회
                .ifPresent(images -> images.stream()
                        .filter(image -> image.getProduct() == null) // 상품과 연결되지 않은 이미지만 필터링
                        .forEach(image -> image.assignProduct(product)) // 해당 이미지를 상품에 할당
                );
    }

    /**
     * [private 메서드]
     * - 이미지 목록 조회
     * - 이미지 이름을 기준으로 이미지 목록을 조회
     * - 조회된 이미지가 없다면 예외 발생, List응답은 빈값으로 처리되어 Optional처리가 되지 않아 Empty로 직접 체크
     */
    private List<Image> findByImageNames(List<String> imageNames) {
        List<Image> images = imageRepository.findByNameIn(imageNames);
        if (images.isEmpty()) throw new CustomApplicationException(ErrorCode.IMAGE_NAME_MISSING);
        return images;
    }
}
