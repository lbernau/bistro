package io.github.lbernau.bistro.controller;

import io.github.lbernau.bistro.dto.CreateOrderItemDto;
import io.github.lbernau.bistro.dto.CreateOrderRequest;
import io.github.lbernau.bistro.dto.OrderResponse;
import io.github.lbernau.bistro.persistence.entity.Order;
import io.github.lbernau.bistro.persistence.entity.OrderItem;
import io.github.lbernau.bistro.persistence.entity.Product;
import io.github.lbernau.bistro.persistence.repository.OrderRepository;
import io.github.lbernau.bistro.persistence.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                properties = {"io.github.lbernau.bistro.import.enabled=false"})
public class OrderControllerTestIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @DynamicPropertySource
    static void dbProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @BeforeEach
    void setUp(@Autowired ProductRepository productRepository) {
        orderRepository.deleteAll();

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
    void shouldCreateOrder() {
        final CreateOrderRequest request = CreateOrderRequest.builder()
                                                             .tableNumber(10)
                                                             .orderedItems(List.of(CreateOrderItemDto.builder()
                                                                                                     .productId(1L)
                                                                                                     .quantity(2)
                                                                                                     .build()))
                                                             .build();

        ResponseEntity<OrderResponse> response = restTemplate.postForEntity("/orders", request, OrderResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.hasBody()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()
                           .getReceipt()).contains("Pizza Hawaii");

        assertThat(orderRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldFailToCreateOrderMissingProduct() {
        final CreateOrderRequest request = CreateOrderRequest.builder()
                                                             .tableNumber(10)
                                                             .orderedItems(List.of(CreateOrderItemDto.builder()
                                                                                                     .productId(10L)
                                                                                                     .quantity(2)
                                                                                                     .build()))
                                                             .build();

        final ResponseEntity<ProblemDetail> response = restTemplate.postForEntity("/orders", request, ProblemDetail.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.hasBody()).isTrue();

        final ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getTitle()).isEqualTo("Order validation failed");
        assertThat(problemDetail.getProperties()).isNotEmpty();
        assertThat(problemDetail.getProperties()).containsKey("errors");
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) problemDetail.getProperties()
                                                                        .get("errors");

        assertThat(errors).hasSize(1)
                          .containsEntry("orderedItems[0].productId", "Product 10 does not exist.");
    }

    @Test
    void shouldFailToCreateOrderQuantityZero() {
        final CreateOrderRequest request = CreateOrderRequest.builder()
                                                             .tableNumber(10)
                                                             .orderedItems(List.of(CreateOrderItemDto.builder()
                                                                                                     .productId(1L)
                                                                                                     .quantity(0)
                                                                                                     .build()))
                                                             .build();

        final ResponseEntity<ProblemDetail> response = restTemplate.postForEntity("/orders", request, ProblemDetail.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.hasBody()).isTrue();

        final ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getTitle()).isEqualTo("Validation failed");
        assertThat(problemDetail.getProperties()).isNotEmpty();
        assertThat(problemDetail.getProperties()).containsKey("errors");
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) problemDetail.getProperties()
                                                                        .get("errors");

        assertThat(errors).hasSize(1)
                          .containsEntry("orderedItems[0].quantity", "Quantity cannot be less then 1.");
    }

    @Test
    void shouldFindOrder() {
        final Order order = orderRepository.save(
                        Order.builder()
                             .orderDate(new Date())
                             .tableNumber(10)
                             .orderItems(List.of(OrderItem.builder()
                                                          .productId(1L)
                                                          .productName("Pizza Hawaii")
                                                          .quantity(2)
                                                          .unitPrice(BigDecimal.valueOf(10))
                                                          .lineTotal(BigDecimal.valueOf(20))
                                                          .build()))
                             .subtotal(BigDecimal.valueOf(20))
                             .discountPercentage(BigDecimal.valueOf(10))
                             .discountAmount(BigDecimal.valueOf(2))
                             .total(BigDecimal.valueOf(18))
                             .build());

        ResponseEntity<OrderResponse> response = restTemplate.getForEntity("/orders/" + order.getId(), OrderResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.hasBody()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()
                           .getReceipt()).contains("Pizza Hawaii");
    }

    @Test
    void shouldNotFindOrder() {
        final UUID randomUUID = UUID.randomUUID();
        ResponseEntity<ProblemDetail> response = restTemplate.getForEntity("/orders/" + randomUUID, ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.hasBody()).isTrue();
        assertThat(response.getBody()).isNotNull();

        final ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getTitle()).isEqualTo("Order with id: " + randomUUID + " not found");
    }
}
