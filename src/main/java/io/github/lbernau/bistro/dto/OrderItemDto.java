package io.github.lbernau.bistro.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItemDto {

    private String orderItemId;

    private Long productId;

    private String productName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal lineTotal;
}
