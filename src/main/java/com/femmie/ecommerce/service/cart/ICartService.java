package com.femmie.ecommerce.service.cart;

import java.math.BigDecimal;

import com.femmie.ecommerce.model.Cart;

public interface ICartService {

    Cart getCart(Long id);

    void clearCart(Long id);

    BigDecimal getTotalPrice(Long id);

    Long initializedNewCart();
}
