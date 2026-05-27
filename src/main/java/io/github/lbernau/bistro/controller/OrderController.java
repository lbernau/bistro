package io.github.lbernau.bistro.controller;

import io.github.lbernau.bistro.dto.CreateOrderRequest;
import io.github.lbernau.bistro.dto.OrderResponse;
import io.github.lbernau.bistro.persistence.entity.Order;
import io.github.lbernau.bistro.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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

    @Operation(summary = "Place a new order")
    @ApiResponses({
                    @ApiResponse(
                                    responseCode = "201",
                                    description = "Order placed successfully",
                                    content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = OrderResponse.class)
                                    )
                    ),
                    @ApiResponse(
                                    responseCode = "400",
                                    description = "Failed to validate order",
                                    content = @Content(
                                                    mediaType = "application/problem+json",
                                                    schema = @Schema(implementation = ProblemDetail.class)
                                    )
                    )
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        final Order order = orderService.createOrder(createOrderRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(OrderResponse.builder()
                                                      .orderId(order.getId())
                                                      .receipt(order.toReceipt())
                                                      .build());
    }

    @Operation(summary = "Get order by id")
    @ApiResponses({
                    @ApiResponse(
                                    responseCode = "200",
                                    description = "Order found",
                                    content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = OrderResponse.class)
                                    )
                    ),
                    @ApiResponse(
                                    responseCode = "404",
                                    description = "Order not found.",
                                    content = @Content(
                                                    mediaType = "application/problem+json",
                                                    schema = @Schema(implementation = ProblemDetail.class)
                                    )
                    )
    })
    @GetMapping(path = "/{orderId}")
    public ResponseEntity<OrderResponse> getOrderbyId(@PathVariable final UUID orderId) {
        final Order order = orderService.findOrderById(orderId);
        return ResponseEntity.status(HttpStatus.OK)
                             .body(OrderResponse.builder()
                                                      .orderId(order.getId())
                                                      .receipt(order.toReceipt())
                                                      .build());
    }
}
