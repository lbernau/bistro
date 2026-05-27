package io.github.lbernau.bistro.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateOrderItemDto {

    @NotNull(message = "ProductId must not be null.")
    private Long productId;

    @Positive(message = "Quantity cannot be less then 1.")
    private Integer quantity;
}
