package io.github.lbernau.bistro.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateOrderRequest {

    @NotNull(message = "Table number must not be null.")
    @Positive(message = "Table number cannot be zero or negative")
    private Integer tableNumber;

    @Builder.Default
    @NotEmpty(message = "You need to order at least one item")
    private List<@Valid CreateOrderItemDto> orderedItems = new ArrayList<>();
}
