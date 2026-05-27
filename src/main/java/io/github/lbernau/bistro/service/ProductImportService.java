package io.github.lbernau.bistro.service;

import io.github.lbernau.bistro.dto.ProductCsvRow;
import io.github.lbernau.bistro.parser.ProductCsvParser;
import io.github.lbernau.bistro.persistence.entity.Product;
import io.github.lbernau.bistro.util.EntityDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImportService {

    private final ProductService productService;

    private final ProductCsvParser productCsvParser;

    @SneakyThrows
    public void importProductCsv(final File file, MessageHeaders headers) {
        final List<ProductCsvRow> products = productCsvParser.parse(file);
        if (!CollectionUtils.isEmpty(products)) {
            productService.saveProducts(products.stream()
                                                .map(productCsvRow -> EntityDtoMapper.convert(productCsvRow, Product.class))
                                                .toList());
        }
    }
}