package io.github.lbernau.bistro.service;

import io.github.lbernau.bistro.dto.ProductCsvRow;
import io.github.lbernau.bistro.exception.CsvImportException;
import io.github.lbernau.bistro.parser.ProductCsvParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageHeaders;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductImportServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductCsvParser csvParser;

    @InjectMocks
    private ProductImportService productImportService;

    @Test
    void shouldImportProductsSuccessfully() {
        List<ProductCsvRow> rows = List.of(new ProductCsvRow());
        when(csvParser.parse(any())).thenReturn(rows);
        productImportService.importProductCsv(new File("dummy.csv"), new MessageHeaders(Map.of()));
        verify(productService, times(1)).saveProducts(anyList());

        verify(productService, times(1)).saveProducts(anyList());
    }

    @Test
    void shouldThrowExceptionWhenCsvHasErrors() {
        when(csvParser.parse(any())).thenThrow(new CsvImportException(List.of("error")));
        assertThrows(CsvImportException.class, () ->
                        productImportService.importProductCsv(new File("dummy.csv"), new MessageHeaders(Map.of()))
        );

        verifyNoInteractions(productService);
    }

    @Test
    void shouldNotCallServiceWhenCsvIsEmpty() {
        when(csvParser.parse(any())).thenReturn(List.of());
        productImportService.importProductCsv(new File("dummy.csv"), new MessageHeaders(Map.of()));

        verifyNoInteractions(productService);
    }
}