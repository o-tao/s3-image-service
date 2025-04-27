package example.product.controller;

import example.image.controller.dto.ImageUploadResponse;
import example.product.controller.dto.ProductCreateRequest;
import example.product.controller.dto.ProductResponse;
import example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductResponse<ImageUploadResponse> createProduct(@RequestBody ProductCreateRequest productCreateRequest) {
        return productService.createProduct(productCreateRequest.toCreate());
    }
}
