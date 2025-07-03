package com.femmie.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.femmie.ecommerce.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    void deleteAllByCartId(Long id);
}
