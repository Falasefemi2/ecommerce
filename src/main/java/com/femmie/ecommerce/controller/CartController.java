package com.femmie.ecommerce.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.femmie.ecommerce.exception.ResourceNotFoundException;
import com.femmie.ecommerce.model.Cart;
import com.femmie.ecommerce.response.ApiResponse;
import com.femmie.ecommerce.service.cart.ICartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/carts")
@Slf4j
public class CartController {

    private static final String CART_NOT_FOUND_MESSAGE = "Cart not found";
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    private final ICartService cartService;

    @GetMapping("/{cartId}")
    public ResponseEntity<ApiResponse> getCart(@PathVariable Long cartId) {
        try {
            Cart cart = cartService.getCart(cartId);
            ApiResponse response = new ApiResponse("Cart fetched successfully", cart);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(CART_NOT_FOUND_MESSAGE, null));
        } catch (Exception e) {
            log.error("Error fetching cart with ID: {}", cartId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(INTERNAL_SERVER_ERROR, null));
        }
    }

    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<ApiResponse> clearCart(@PathVariable Long cartId) {
        try {
            cartService.clearCart(cartId);
            ApiResponse response = new ApiResponse("Cart cleared successfully", null);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(CART_NOT_FOUND_MESSAGE, null));
        } catch (Exception e) {
            log.error("Error clearing cart with ID: {}", cartId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(INTERNAL_SERVER_ERROR, null));
        }
    }

    @GetMapping("/{cartId}/total")
    public ResponseEntity<ApiResponse> getTotalAmount(@PathVariable Long cartId) {
        try {
            BigDecimal total = cartService.getTotalPrice(cartId);
            ApiResponse response = new ApiResponse("Cart total fetched successfully", total);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(CART_NOT_FOUND_MESSAGE, null));
        } catch (Exception e) {
            log.error("Error fetching cart total for ID: {}", cartId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(INTERNAL_SERVER_ERROR, null));
        }
    }
}
