package com.femmie.ecommerce.service.cart;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.femmie.ecommerce.exception.ResourceNotFoundException;
import com.femmie.ecommerce.model.Cart;
import com.femmie.ecommerce.repository.CartItemRepository;
import com.femmie.ecommerce.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {

    private static final String CART_NOT_FOUND_MESSAGE = "Cart not found";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Cart getCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CART_NOT_FOUND_MESSAGE));

        BigDecimal totalAmount = getTotalPrice(id);
        cart.setTotalAmount(totalAmount);

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CART_NOT_FOUND_MESSAGE));

        if (cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
            cartItemRepository.deleteAll(cart.getCartItems());
            cart.getCartItems().clear();
        }

        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Override
    public BigDecimal getTotalPrice(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CART_NOT_FOUND_MESSAGE));
        BigDecimal total = BigDecimal.ZERO;
        if (cart.getCartItems() != null) {
            total = cart.getCartItems().stream().map(item -> item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return total;
    }

    @Override
    public Long initializedNewCart() {
        Cart newCart = new Cart();
        newCart.setTotalAmount(BigDecimal.ZERO);
        return cartRepository.save(newCart).getId();
    }

}
