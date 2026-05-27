package io.github.lbernau.bistro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lbernau.bistro.dto.ProductDto;
import io.github.lbernau.bistro.exception.ResourceNotFoundException;
import io.github.lbernau.bistro.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class,
                properties = {"io.github.lbernau.bistro.import.enabled=false"})
class ProductControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void shouldReturnOk()
                    throws Exception {
        mockMvc.perform(get("/products"))
               .andExpect(status().isOk());

        verify(productService).getProductsAsDto();
    }

    @Test
    void findProductByIdShouldReturnNotFound()
                    throws Exception {
        when(productService.findProductDtoById(anyLong())).thenThrow(new ResourceNotFoundException("Product with id: 123 not found"));
        mockMvc.perform(get("/products/123"))
               .andExpect(status().isNotFound());

        verify(productService).findProductDtoById(123L);
    }

    @Test
    void findProductByIdShouldReturnOk()
                    throws Exception {
        final ProductDto pizza = ProductDto.builder()
                                           .productId(123L)
                                           .productName("Pizza Hawaii")
                                           .price(BigDecimal.TEN)
                                           .build();
        when(productService.findProductDtoById(anyLong())).thenReturn(pizza);

        mockMvc.perform(get("/products/123"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(content().json(new ObjectMapper().writeValueAsString(pizza)));

        verify(productService).findProductDtoById(123L);
    }
}