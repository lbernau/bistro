package io.github.lbernau.bistro.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class OrderItem
                implements Serializable {

    @Serial
    private static final long serialVersionUID = -1455177073060723712L;

    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID orderItemId;

    private Long productId;

    private String productName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal lineTotal;
}
