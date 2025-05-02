package example.product.controller;

import example.image.controller.dto.ImageResponse;
import example.product.controller.dto.ProductRequest;
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
    public ProductResponse<ImageResponse> createProduct(@RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest.toCreate());
    }

    @GetMapping("/{productId}")
    public ProductResponse<ImageResponse> detailProduct(@PathVariable Long productId) {
        return productService.detailProduct(productId);
    }

    @PutMapping("/{productId}")
    public ProductResponse<ImageResponse> updateProduct(@PathVariable Long productId,
                                                        @RequestBody ProductRequest productRequest
    ) {
        return productService.updateProduct(productRequest.toUpdate(productId));
    }
}
