package io.github.lbernau.bistro.service;

import io.github.lbernau.bistro.dto.ProductDto;
import io.github.lbernau.bistro.exception.ResourceNotFoundException;
import io.github.lbernau.bistro.persistence.entity.Product;
import io.github.lbernau.bistro.persistence.repository.ProductRepository;
import io.github.lbernau.bistro.util.EntityDtoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductDto> getProductsAsDto() {
        return productRepository.findAll()
                                .stream()
                                .map(product -> EntityDtoMapper.convert(product, ProductDto.class))
                                .toList();
    }

    public ProductDto findProductDtoById(Long productId) {
        return productRepository.findById(productId)
                                .map(product -> EntityDtoMapper.convert(product, ProductDto.class))
                                .orElseThrow(() -> new ResourceNotFoundException("Product with id: " + productId + " not found"));
    }

    public void saveProducts(List<Product> products) {
        if (!CollectionUtils.isEmpty(products)) {
            productRepository.saveAll(products);
        }
    }

    public boolean existsById(@NotNull(message = "ProductId must not be null.") Long productId) {
        return productRepository.existsById(productId);
    }

    public Product getProduct(@NotNull(message = "ProductId must not be null.") Long productId) {
        return productRepository.findById(productId)
                                .orElse(null);
    }
}
