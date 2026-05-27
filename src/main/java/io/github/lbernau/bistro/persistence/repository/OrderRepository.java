package io.github.lbernau.bistro.persistence.repository;

import io.github.lbernau.bistro.persistence.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository
                extends JpaRepository<Order, UUID> {

}
