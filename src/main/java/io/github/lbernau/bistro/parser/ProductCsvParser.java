package io.github.lbernau.bistro.parser;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import io.github.lbernau.bistro.dto.ProductCsvRow;
import io.github.lbernau.bistro.exception.CsvImportException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCsvParser {

    @SneakyThrows
    public List<ProductCsvRow> parse(File file) {
        try (final Reader inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            final CsvToBean<ProductCsvRow> csvToBean = new CsvToBeanBuilder<ProductCsvRow>(inputStreamReader).withSeparator(',')
                                                                                                             .withType(ProductCsvRow.class)
                                                                                                             .withThrowExceptions(false)
                                                                                                             .build();
            final List<ProductCsvRow> products = new ArrayList<>();
            final List<String> validationErrors = new ArrayList<>();
            try {
                products.addAll(csvToBean.parse());
                for (CsvException exception : csvToBean.getCapturedExceptions()) {
                    validationErrors.add("CSV Error at line " + exception.getLineNumber() + ": " + exception.getMessage());
                }
            } catch (RuntimeException e) {
                validationErrors.add(e.getMessage());
            }

            if (!validationErrors.isEmpty()) {
                throw new CsvImportException(validationErrors);
            }

            return products;
        }
    }
}
