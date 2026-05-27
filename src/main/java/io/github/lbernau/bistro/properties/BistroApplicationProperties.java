package io.github.lbernau.bistro.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "io.github.lbernau.bistro")
public record BistroApplicationProperties(BigDecimal happyHourDiscount, String importFolder, String errorFolder, String successFolder) {

}
