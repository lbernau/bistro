package io.github.lbernau.bistro.controller;

import io.github.lbernau.bistro.dto.CreateOrderRequest;
import io.github.lbernau.bistro.dto.CreateOrderResponse;
import io.github.lbernau.bistro.persistence.entity.Order;
import io.github.lbernau.bistro.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        final Order order = orderService.createOrder(createOrderRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(CreateOrderResponse.builder()
                                                      .receipt(order.toReceipt())
                                                      .build());
    }

    @GetMapping(path = "/{orderId}")
    public ResponseEntity<CreateOrderResponse> getOrderbyId(@PathVariable final UUID orderId) {
        final Order order = orderService.findOrderById(orderId);

        if (order == null) {
            return ResponseEntity
                            .notFound()
                            .build();
        }
        return ResponseEntity.status(HttpStatus.OK)
                             .body(CreateOrderResponse.builder()
                                                      .receipt(order.toReceipt())
                                                      .build());
    }
}
