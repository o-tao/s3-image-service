package example.product.service.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductUpdateInfo {

    private Long id;
    private String name;
    private int price;
    private String description;
    private List<Long> imageIds;

    public ProductUpdateInfo(Long productId, String name, int price, String description, List<Long> imageIds) {
        this.id = productId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageIds = imageIds;
    }
}
