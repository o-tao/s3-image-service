package example.domain.products;

import example.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Comment("상품 명")
    @Column(name = "name",nullable = false)
    private String name;

    @Comment("상품 가격")
    @Column(name = "price", nullable = false)
    private int price;

    @Comment("상품 설명")
    @Column(name = "description", nullable = false)
    private String description;

    public Product(String name, int price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public static Product create(String name, int price, String description) {
        return new Product(name, price, description);
    }
}
