package io.github.lbernau.bistro.controller;

import io.github.lbernau.bistro.dto.ProductDto;
import io.github.lbernau.bistro.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductDto> getProducts() {
        return productService.getProductsAsDto();
    }

    @GetMapping(path = "/{productId}")
    public ResponseEntity<ProductDto> findProductById(@PathVariable final Long productId) {
        final ProductDto productDto = productService.findProductDtoById(productId);

        if (productDto == null) {
            return ResponseEntity
                            .notFound()
                            .build();
        }
        return ResponseEntity.status(HttpStatus.OK)
                             .body(productDto);
    }
}
