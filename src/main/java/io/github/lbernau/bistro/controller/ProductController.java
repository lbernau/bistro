package io.github.lbernau.bistro.controller;

import io.github.lbernau.bistro.dto.OrderResponse;
import io.github.lbernau.bistro.dto.ProductDto;
import io.github.lbernau.bistro.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "List products")
    @ApiResponses({
                    @ApiResponse(
                                    responseCode = "200",
                                    content = @Content(
                                                    mediaType = "application/json",
                                                    array = @ArraySchema(
                                                                    schema = @Schema(implementation = ProductDto.class)
                                                    )
                                    )
                    )
    })
    @GetMapping
    public List<ProductDto> getProducts() {
        return productService.getProductsAsDto();
    }

    @Operation(summary = "Get product by id.")
    @ApiResponses({
                    @ApiResponse(
                                    responseCode = "200",
                                    description = "Order found",
                                    content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ProductDto.class)
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
    @GetMapping(path = "/{productId}")
    public ResponseEntity<ProductDto> findProductById(@PathVariable final Long productId) {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(productService.findProductDtoById(productId));
    }
}
