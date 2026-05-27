package io.github.lbernau.bistro.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResponse implements Serializable {

    private String orderId;

    private Date orderDate;

    private Integer tableNumber;

    @Builder.Default
    private List<OrderItemDto> orderItems = new ArrayList<>();

    private BigDecimal subtotal;

    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    private BigDecimal total;
}
