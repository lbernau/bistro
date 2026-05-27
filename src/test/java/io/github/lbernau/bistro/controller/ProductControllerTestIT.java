package io.github.lbernau.bistro.controller;

import io.github.lbernau.bistro.dto.ProductDto;
import io.github.lbernau.bistro.persistence.entity.Product;
import io.github.lbernau.bistro.persistence.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                properties = {"io.github.lbernau.bistro.import.enabled=false"})
class ProductControllerTestIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void dbProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @BeforeEach
    void setup(@Autowired ProductRepository productRepository) {
        productRepository.deleteAll();
        productRepository.saveAll(List.of(
                        Product.builder()
                               .productId(1L)
                               .productName("Pizza Hawaii")
                               .price(BigDecimal.TEN)
                               .build(),
                        Product.builder()
                               .productId(2L)
                               .productName("Cola")
                               .price(BigDecimal.ONE)
                               .build())
        );
    }

    @Test
    void shouldFindProductById() {
        final long productId = 1L;
        ResponseEntity<ProductDto> response = restTemplate.getForEntity("/products/" + productId, ProductDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.hasBody()).isTrue();
        final ProductDto productDto = response.getBody();
        assertThat(productDto.getProductName()).isEqualTo("Pizza Hawaii");
    }

    @Test
    void shouldNotFindProductById() {
        final long productId = 10L;
        ResponseEntity<ProblemDetail> response = restTemplate.getForEntity("/products/" + productId, ProblemDetail.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.hasBody()).isTrue();
        assertThat(response.getBody()).isNotNull();

        final ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getTitle()).isEqualTo("Product with id: " + productId + " not found");
    }

    @Test
    void shouldReadProducts() {
        final ResponseEntity<List<ProductDto>> response =
                        restTemplate.exchange("/products", HttpMethod.GET, null, new ParameterizedTypeReference<List<ProductDto>>() {
                        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.hasBody()).isTrue();
        final List<ProductDto> responseBody = response.getBody();
        assertThat(responseBody.size()).isEqualTo(2);
        assertThat(responseBody.getFirst()
                               .getProductName()).isEqualTo("Pizza Hawaii");
    }
}