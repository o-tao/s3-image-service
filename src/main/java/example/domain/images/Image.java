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

    @Comment("업로드 토큰")
    @Column(name = "upload_token", nullable = false)
    private String uploadToken;

    @Comment("이미지 경로")
    @Column(name = "path", nullable = false)
    private String path;

    @Comment("이미지 명")
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    public Image(String uploadToken, String path, String name, Product product) {
        this.uploadToken = uploadToken;
        this.path = path;
        this.name = name;
        this.product = product;
    }
}
