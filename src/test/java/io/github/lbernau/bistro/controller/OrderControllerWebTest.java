package io.github.lbernau.bistro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lbernau.bistro.dto.CreateOrderItemDto;
import io.github.lbernau.bistro.dto.CreateOrderRequest;
import io.github.lbernau.bistro.exception.OrderValidationException;
import io.github.lbernau.bistro.persistence.entity.Order;
import io.github.lbernau.bistro.persistence.entity.OrderItem;
import io.github.lbernau.bistro.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class,
                properties = {"io.github.lbernau.bistro.import.enabled=false"})
class OrderControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void findProductByIdShouldReturnNotFound()
                    throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.findOrderById(any(UUID.class))).thenReturn(null);
        mockMvc.perform(get("/orders/" + orderId))
               .andExpect(status().isNotFound());

        verify(orderService).findOrderById(orderId);
    }

    @Test
    void createOrderShouldReturnCreated()
                    throws Exception {
        final CreateOrderRequest request = CreateOrderRequest.builder()
                                                             .tableNumber(1)
                                                             .orderedItems(List.of(
                                                                             CreateOrderItemDto.builder()
                                                                                               .productId(1L)
                                                                                               .quantity(1)
                                                                                               .build()))
                                                             .build();
        final Order order = Order.builder()
                                 .tableNumber(1)
                                 .orderItems(List.of(
                                                 OrderItem.builder()
                                                          .productId(1L)
                                                          .productName("Pizza Hawaii")
                                                          .unitPrice(BigDecimal.TEN)
                                                          .lineTotal(BigDecimal.TEN)
                                                          .quantity(1)
                                                          .build()
                                 ))
                                 .subtotal(BigDecimal.TEN)
                                 .discountPercentage(BigDecimal.ZERO)
                                 .discountAmount(BigDecimal.ZERO)
                                 .total(BigDecimal.TEN)
                                 .build();
        when(orderService.createOrder(request)).thenReturn(order);
        mockMvc.perform(post("/orders")
                               .content(new ObjectMapper().writeValueAsString(request))
                               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.receipt").value(org.hamcrest.Matchers.containsString(order.toReceipt())));

        verify(orderService).createOrder(request);
    }

    @Test
    void createOrderShouldReturnBadRequest()
                    throws Exception {
        final CreateOrderRequest request = CreateOrderRequest.builder()
                                                             .tableNumber(1)
                                                             .orderedItems(List.of(
                                                                             CreateOrderItemDto.builder()
                                                                                               .productId(1L)
                                                                                               .quantity(1)
                                                                                               .build()))
                                                             .build();
        when(orderService.createOrder(request)).thenThrow(new OrderValidationException(Map.of()));
        mockMvc.perform(post("/orders")
                               .content(new ObjectMapper().writeValueAsString(request))
                               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));

        verify(orderService).createOrder(request);
    }
}