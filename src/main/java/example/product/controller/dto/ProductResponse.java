package example.product.controller.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResponse<T> {

    private Long id;
    private String name;
    private int price;
    private String description;
    private List<T> images;

    public ProductResponse(Long id,
                           String name,
                           int price,
                           String description,
                           List<T> images
    ) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.images = images;
    }

    public static <T> ProductResponse<T> of(Long id,
                                            String name,
                                            int price,
                                            String description,
                                            List<T> images
    ) {
        return new ProductResponse<>(id, name, price, description, images);
    }
}
