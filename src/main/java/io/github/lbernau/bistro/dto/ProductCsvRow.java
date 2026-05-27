package io.github.lbernau.bistro.dto;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCsvRow {

    @CsvBindByName(column = "productId", required = true)
    private long productId;

    @NotBlank
    @CsvBindByName(column = "productName", required = true)
    private String productName;

    @NotNull
    @DecimalMin("0.01")
    @CsvBindByName(column = "price", required = true)
    private BigDecimal price;
}
