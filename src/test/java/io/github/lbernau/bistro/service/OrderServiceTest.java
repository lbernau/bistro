package io.github.lbernau.bistro.service;

import io.github.lbernau.bistro.dto.CreateOrderItemDto;
import io.github.lbernau.bistro.dto.CreateOrderRequest;
import io.github.lbernau.bistro.exception.OrderValidationException;
import io.github.lbernau.bistro.persistence.entity.Order;
import io.github.lbernau.bistro.persistence.entity.Product;
import io.github.lbernau.bistro.persistence.repository.OrderRepository;
import io.github.lbernau.bistro.properties.BistroApplicationProperties;
import io.github.lbernau.bistro.validation.OrderValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private OrderValidator orderValidator;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BistroApplicationProperties properties;

    private Clock clock;

    private OrderService service;

    @BeforeEach
    void setup() {
        // default: no happy hour
        initializeServiceAt(LocalDateTime.of(2026, 5, 27, 10, 30));
    }

    @Test
    void shouldApplyHappyHourDiscount() {
        initializeServiceAt(LocalDateTime.of(2026, 5, 27, 17, 30));

        Product pizza = getPizzaHawaii();
        CreateOrderRequest request =
                        CreateOrderRequest.builder()
                                          .tableNumber(10)
                                          .orderedItems(
                                                          List.of(
                                                                          CreateOrderItemDto.builder()
                                                                                            .productId(1L)
                                                                                            .quantity(2)
                                                                                            .build()
                                                          ))
                                          .build();

        when(orderValidator.validateCreateOrderRequest(request)).thenReturn(Map.of());
        when(productService.getProduct(1L)).thenReturn(pizza);
        when(properties.happyHourDiscount()).thenReturn(BigDecimal.TEN);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = service.createOrder(request);
        assertThat(order.getSubtotal())
                        .isEqualByComparingTo("20.00");
        assertThat(order.getDiscountPercentage())
                        .isEqualByComparingTo("10");
        assertThat(order.getDiscountAmount())
                        .isEqualByComparingTo("2.00");
        assertThat(order.getTotal())
                        .isEqualByComparingTo("18.00");

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldNotApplyDiscountOutsideHappyHour() {
        Product pizza = getPizzaHawaii();
        CreateOrderRequest request =
                        CreateOrderRequest.builder()
                                          .tableNumber(10)
                                          .orderedItems(
                                                          List.of(
                                                                          CreateOrderItemDto.builder()
                                                                                            .productId(1L)
                                                                                            .quantity(2)
                                                                                            .build()
                                                          ))
                                          .build();

        when(orderValidator.validateCreateOrderRequest(request)).thenReturn(Map.of());
        when(productService.getProduct(1L)).thenReturn(pizza);
        when(properties.happyHourDiscount()).thenReturn(BigDecimal.TEN);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = service.createOrder(request);
        assertThat(order.getSubtotal())
                        .isEqualByComparingTo("20.00");
        assertThat(order.getDiscountPercentage())
                        .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(order.getDiscountAmount())
                        .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(order.getTotal())
                        .isEqualByComparingTo(order.getSubtotal());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldCalculateSubtotalCorrectly() {
        CreateOrderRequest request =
                        CreateOrderRequest.builder()
                                          .tableNumber(10)
                                          .orderedItems(
                                                          List.of(
                                                                          CreateOrderItemDto.builder()
                                                                                            .productId(1L)
                                                                                            .quantity(2)
                                                                                            .build(),
                                                                          CreateOrderItemDto.builder()
                                                                                            .productId(2L)
                                                                                            .quantity(1)
                                                                                            .build()
                                                          ))
                                          .build();

        when(orderValidator.validateCreateOrderRequest(request)).thenReturn(Map.of());
        when(productService.getProduct(1L)).thenReturn(getPizzaHawaii());
        when(productService.getProduct(2L)).thenReturn(Product.builder()
                                                              .productId(2L)
                                                              .productName("Cola")
                                                              .price(BigDecimal.valueOf(2.50))
                                                              .build());

        when(properties.happyHourDiscount()).thenReturn(BigDecimal.ZERO);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = service.createOrder(request);

        assertThat(order.getSubtotal()).isEqualByComparingTo("22.50");
        assertThat(order.getOrderItems()).hasSize(2);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldThrowValidationExceptionWhenProductDoesNotExist() {
        CreateOrderRequest request =
                        CreateOrderRequest.builder()
                                          .tableNumber(10)
                                          .orderedItems(
                                                          List.of(
                                                                          CreateOrderItemDto.builder()
                                                                                            .productId(999L)
                                                                                            .quantity(1)
                                                                                            .build()
                                                          )
                                          )
                                          .build();

        Map<String, String> validationErrors = Map.of("orderedItems[0].productId", " Product 999 does not exist.");
        when(orderValidator.validateCreateOrderRequest(request)).thenReturn(validationErrors);

        assertThatThrownBy(() -> service.createOrder(request)).isInstanceOf(OrderValidationException.class)
                                                              .hasMessageContaining("Order validation failed.");

        verifyNoInteractions(productService);
        verifyNoInteractions(orderRepository);
    }

    @Test
    void shouldLoadProductsOnlyOnce() {
        CreateOrderRequest request =
                        CreateOrderRequest.builder()
                                          .tableNumber(10)
                                          .orderedItems(
                                                          List.of(
                                                                          CreateOrderItemDto.builder()
                                                                                            .productId(1L)
                                                                                            .quantity(2)
                                                                                            .build(),

                                                                          CreateOrderItemDto.builder()
                                                                                            .productId(1L)
                                                                                            .quantity(3)
                                                                                            .build()
                                                          )
                                          )
                                          .build();

        Product pizza = getPizzaHawaii();

        when(orderValidator.validateCreateOrderRequest(request)).thenReturn(Map.of());
        when(productService.getProduct(1L)).thenReturn(pizza);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.createOrder(request);
        verify(productService, times(1)).getProduct(1L);
        verify(orderRepository).save(any(Order.class));
    }

    private void initializeServiceAt(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.of("Europe/Berlin");
        clock = Clock.fixed(localDateTime.atZone(zone).toInstant(), zone);
        service = new OrderService(clock, productService, orderValidator, orderRepository, properties);
    }


    private Product getPizzaHawaii() {
        return Product.builder()
                      .productId(1L)
                      .productName("Pizza Hawaii")
                      .price(BigDecimal.valueOf(10))
                      .build();
    }
}