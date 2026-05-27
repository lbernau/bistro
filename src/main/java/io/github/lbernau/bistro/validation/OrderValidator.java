package io.github.lbernau.bistro.validation;

import io.github.lbernau.bistro.dto.CreateOrderItemDto;
import io.github.lbernau.bistro.dto.CreateOrderRequest;
import io.github.lbernau.bistro.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderValidator {

    private final ProductService productService;

    public Map<String, String> validateCreateOrderRequest(CreateOrderRequest createOrderRequest) {
        Map<String, String> validationErrors = new HashMap<>();
        int lineNumber = 0;
        for (CreateOrderItemDto item : createOrderRequest.getOrderedItems()) {
            if (!productService.existsById(item.getProductId())) {
                validationErrors.put("orderedItems[" + lineNumber + "].productId", "Product " + item.getProductId() + " does not exist.");
            }
            lineNumber++;
        }
        return validationErrors;
    }
}
