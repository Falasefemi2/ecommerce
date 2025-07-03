package com.femmie.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.femmie.ecommerce.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
