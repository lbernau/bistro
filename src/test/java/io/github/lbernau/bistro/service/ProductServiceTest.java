package io.github.lbernau.bistro.service;

import io.github.lbernau.bistro.dto.ProductDto;
import io.github.lbernau.bistro.exception.CsvImportException;
import io.github.lbernau.bistro.exception.ResourceNotFoundException;
import io.github.lbernau.bistro.persistence.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageHeaders;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setup() {
        productService = new ProductService(productRepository);
    }

    @Test
    void shouldNotSaveEmptyProducts() {
        productService.saveProducts(List.of());
        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> productService.findProductDtoById(1L));
        verify(productRepository).findById(1L);
    }

    // skipped test for other methods.
    // At the current state the product service only wraps repository methods.
}