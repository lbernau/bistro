package io.github.lbernau.bistro.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "orders")
public class Order
                implements Serializable {

    @Serial
    private static final long serialVersionUID = -2480419508055340177L;

    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;

    @Builder.Default
    private Date orderDate = new Date();

    @Column(nullable = false)
    private Integer tableNumber;

    @Builder.Default
    @JoinColumn(name = "order_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    private BigDecimal subtotal;

    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    private BigDecimal total;

    public String toReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("-------------------------\n");
        sb.append("Table Nr. %s\n".formatted(tableNumber));
        sb.append("-------------------------\n");
        for (OrderItem item : orderItems) {
            sb.append("%d x %s @ %.2f = %.2f%n".formatted(
                            item.getQuantity(), item.getProductName(), item.getUnitPrice(), item.getLineTotal()));
        }

        sb.append("-------------------------\n");
        sb.append("Subtotal: %.2f%n".formatted(subtotal));
        sb.append("Discount: %.2f%%%n".formatted(discountPercentage));
        sb.append("Total: %.2f%n".formatted(discountAmount));

        return sb.toString();
    }
}
