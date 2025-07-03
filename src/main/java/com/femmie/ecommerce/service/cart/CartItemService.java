package com.femmie.ecommerce.service.cart;

import java.util.HashSet;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.femmie.ecommerce.exception.ResourceNotFoundException;
import com.femmie.ecommerce.model.Cart;
import com.femmie.ecommerce.model.CartItem;
import com.femmie.ecommerce.model.Product;
import com.femmie.ecommerce.repository.CartItemRepository;
import com.femmie.ecommerce.repository.CartRepository;
import com.femmie.ecommerce.service.product.IProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {

    private final CartItemRepository cartItemRepository;
    private final IProductService productService;
    private final ICartService cartService;
    private final CartRepository cartRepository;

    @Override
    public void addCartItem(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCart(cartId);
        Optional<Product> product = productService.findProductById(productId);
        if (cart.getCartItems() == null) {
            cart.setCartItems(new HashSet<>());
        }

        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            product.ifPresent(cartItem::setProduct);
            if (product.isPresent()) {
                cartItem.setUnitPrice(product.get().getPrice());
            }
            cartItem.setQuantity(quantity);
            cart.getCartItems().add(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        cartItem.setTotalPrice();
        cart.setTotalAmount(cartService.getTotalPrice(cartId));
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        CartItem item = getCartItem(cartId, productId);

        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);

        cart.setTotalAmount(cartService.getTotalPrice(cartId));
        cartRepository.save(cart);
    }

    @Override
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCart(cartId);
        CartItem item = getCartItem(cartId, productId);

        if (quantity <= 0) {
            cart.getCartItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            item.setTotalPrice();
            cartItemRepository.save(item);
        }

        cart.setTotalAmount(cartService.getTotalPrice(cartId));
        cartRepository.save(cart);
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        return cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found for productId: " + productId));
    }

}
