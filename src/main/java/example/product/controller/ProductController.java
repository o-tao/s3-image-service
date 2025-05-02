package example.product.controller;

import example.image.controller.dto.ImageResponse;
import example.product.controller.dto.ProductCreateRequest;
import example.product.controller.dto.ProductResponse;
import example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductResponse<ImageResponse> createProduct(@RequestBody ProductCreateRequest productCreateRequest) {
        return productService.createProduct(productCreateRequest.toCreate());
    }

    @GetMapping("/{productId}")
    public ProductResponse<ImageResponse> detailProduct(@PathVariable Long productId) {
        return productService.detailProduct(productId);
    }
}
