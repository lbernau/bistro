package io.github.lbernau.bistro.service;

import io.github.lbernau.bistro.dto.CreateOrderItemDto;
import io.github.lbernau.bistro.dto.CreateOrderRequest;
import io.github.lbernau.bistro.exception.OrderValidationException;
import io.github.lbernau.bistro.exception.ResourceNotFoundException;
import io.github.lbernau.bistro.persistence.entity.Order;
import io.github.lbernau.bistro.persistence.entity.OrderItem;
import io.github.lbernau.bistro.persistence.entity.Product;
import io.github.lbernau.bistro.persistence.repository.OrderRepository;
import io.github.lbernau.bistro.properties.BistroApplicationProperties;
import io.github.lbernau.bistro.validation.OrderValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final Clock clock;

    private final ProductService productService;

    private final OrderValidator orderValidator;

    private final OrderRepository orderRepository;

    private final BistroApplicationProperties properties;

    public Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                              .orElseThrow(() -> new ResourceNotFoundException("Order with id: " + orderId + " not found"));
    }

    public Order createOrder(CreateOrderRequest createOrderRequest) {
        final Map<String, String> validationErrors = orderValidator.validateCreateOrderRequest(createOrderRequest);
        if (!validationErrors.isEmpty()) {
            throw new OrderValidationException(validationErrors);
        }

        final Map<Long, Product> productMap = loadProducts(createOrderRequest);
        final Order order = Order.builder()
                                 .orderDate(new Date())
                                 .tableNumber(createOrderRequest.getTableNumber())
                                 .orderItems(createOrderItems(createOrderRequest, productMap))
                                 .build();

        applyPricing(order);
        orderRepository.save(order);

        return order;
    }

    private void applyPricing(Order order) {
        final BigDecimal subtotal = order.getOrderItems()
                                         .stream()
                                         .map(OrderItem::getLineTotal)
                                         .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setSubtotal(subtotal);
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (properties.happyHourDiscount() != null && isHappyHour()) {
            order.setDiscountPercentage(properties.happyHourDiscount());
            discountAmount = subtotal.multiply(properties.happyHourDiscount())
                                     .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        order.setDiscountAmount(discountAmount);
        final BigDecimal finalTotal = subtotal.subtract(discountAmount)
                                              .setScale(2, RoundingMode.HALF_UP);
        order.setTotal(finalTotal);
    }

    private @NonNull List<OrderItem> createOrderItems(CreateOrderRequest createOrderRequest, Map<Long, Product> productMap) {
        return createOrderRequest.getOrderedItems()
                                 .stream()
                                 .map(createOrderItem -> {
                                     final Product productDto =
                                                     productMap.get(createOrderItem.getProductId());
                                     final BigDecimal lineTotal = productDto.getPrice()
                                                                            .multiply(new BigDecimal(
                                                                                            createOrderItem.getQuantity()));
                                     return OrderItem.builder()
                                                     .productId(createOrderItem.getProductId())
                                                     .quantity(createOrderItem.getQuantity())
                                                     .productName(productDto.getProductName())
                                                     .unitPrice(productDto.getPrice())
                                                     .lineTotal(lineTotal)
                                                     .build();
                                 })
                                 .toList();
    }

    private @NonNull Map<Long, Product> loadProducts(CreateOrderRequest createOrderRequest) {
        Map<Long, Product> productMap = new HashMap<>();
        for (CreateOrderItemDto item : createOrderRequest.getOrderedItems()) {
            if (!productMap.containsKey(item.getProductId())) {
                // getProduct may return null, but we checked for existence before
                productMap.put(item.getProductId(), productService.getProduct(item.getProductId()));
            }
        }
        return productMap;
    }

    private boolean isHappyHour() {
        final LocalTime now = LocalTime.now(clock);
        return !now.isBefore(LocalTime.of(17, 0))
                        && !now.isAfter(LocalTime.of(19, 0));
    }
}
