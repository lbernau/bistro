package io.github.lbernau.bistro.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Product
                implements Serializable {

    @Serial
    private static final long serialVersionUID = -1334559555987363171L;

    @Id
    private Long productId;

    private String productName;

    private BigDecimal price;
}
