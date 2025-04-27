package example.product.service.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCreateInfo {

    private String name;
    private int price;
    private String description;
    private List<String> imageNames;

    public ProductCreateInfo(String name, int price, String description, List<String> imageNames) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageNames = imageNames;
    }
}
