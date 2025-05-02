package example.domain.images.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import example.domain.images.Image;
import example.domain.products.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static example.domain.images.QImage.image;

@Repository
@RequiredArgsConstructor
public class ImageQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * [image <-> productId 매핑해제]
     * - 해당 productId를 가진 상품에 매핑된 이미지들의 `product` 필드를 null로 설정
     */
    public void clearProductFromImages(Long productId) {
        jpaQueryFactory.update(image)
                .set(image.product, (Product) null)
                .where(image.product.id.eq(productId))
                .execute();
    }

    /**
     * [image <-> productId 매핑]
     * - 요청받은 이미지 ID에 해당하는 이미지들의 `product` 필드를 주어진 `productId`로 업데이트
     */
    public void assignProduct(Long productId, List<Long> imageIds) {
        jpaQueryFactory.update(image)
                .set(image.product.id, productId)
                .where(image.id.in(imageIds))
                .execute();
    }

    /**
     * [고아 이미지 조회]
     * - productId가 null 인 이미지 중 createdAt이 주어진 기준(threshold)보다 오래된 것들만 조회
     */
    public List<Image> findOldUnlinkedImages(LocalDateTime threshold) {
        return jpaQueryFactory.selectFrom(image)
                .where(
                        image.product.isNull(),
                        image.createdAt.before(threshold)
                )
                .fetch();
    }
}
