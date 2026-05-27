package io.github.lbernau.bistro.persistence.repository;

import io.github.lbernau.bistro.persistence.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
                extends JpaRepository<Product, Long> {

}
