package example.product.controller.dto;

import example.product.service.dto.ProductCreateInfo;
import example.product.service.dto.ProductUpdateInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductRequest {

    private String name;
    private int price;
    private String description;
    private List<Long> imageIds;

    public ProductRequest(String name, int price, String description, List<Long> imageIds) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageIds = imageIds;
    }

    // 상품 생성 정보 DTO로 데이터 전달
    public ProductCreateInfo toCreate() {
        return new ProductCreateInfo(name, price, description, imageIds);
    }

    // 상품 수정 정보 DTO로 데이터 전달
    public ProductUpdateInfo toUpdate(Long productId) {
        return new ProductUpdateInfo(productId, name, price, description, imageIds);
    }
}
