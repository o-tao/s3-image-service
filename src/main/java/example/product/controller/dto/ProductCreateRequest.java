package example.product.controller.dto;

import example.product.service.dto.ProductCreateInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCreateRequest {

    private String name;
    private int price;
    private String description;
    private List<String> imageNames;

    public ProductCreateRequest(String name, int price, String description, List<String> imageNames) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageNames = imageNames;
    }

    public ProductCreateInfo toCreate() {
        return new ProductCreateInfo(name, price, description, imageNames);
    }
}
