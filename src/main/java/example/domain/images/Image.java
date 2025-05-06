package example.domain.images;

import example.domain.BaseEntity;
import example.domain.products.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {

    @Comment("이미지 경로")
    @Column(name = "path", nullable = false)
    private String path;

    @Comment("이미지 명")
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    public Image(String path, String imageName, Product product) {
        this.path = path;
        this.name = imageName;
        this.product = product;
    }

    // 상품과 연결되지 않은 임시 이미지 생성 (product = null)
    public static Image create(String path, String imageName) {
        return new Image(path, imageName, null);
    }

    // 이미지 <-> 상품 매핑
    public void assignProduct(Product product) {
        this.product = product;
    }
}
