package com.femmie.ecommerce.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.femmie.ecommerce.exception.ResourceNotFoundException;
import com.femmie.ecommerce.response.ApiResponse;
import com.femmie.ecommerce.service.cart.ICartItemService;
import com.femmie.ecommerce.service.cart.ICartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cartsItem")
@Slf4j
public class CartItemController {

    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    private final ICartItemService cartItemService;
    private final ICartService cartService;

    @PostMapping("/{cartId}/items")
    public ResponseEntity<ApiResponse> addItemToCart(
            @PathVariable Long cartId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            cartItemService.addCartItem(cartId, productId, quantity);
            ApiResponse response = new ApiResponse("Item added to cart successfully", null);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Cart or product not found", null));
        } catch (Exception e) {
            log.error("Error adding item to cart. CartId: {}, ProductId: {}", cartId, productId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(INTERNAL_SERVER_ERROR, null));
        }
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse> addItemToNewCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            Long cartId = cartService.initializedNewCart();
            cartItemService.addCartItem(cartId, productId, quantity);
            ApiResponse response = new ApiResponse("Item added to new cart successfully",
                    Map.of("cartId", cartId));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Product not found", null));
        } catch (Exception e) {
            log.error("Error adding item to new cart. ProductId: {}", productId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(INTERNAL_SERVER_ERROR, null));
        }
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<ApiResponse> updateItemQuantity(
            @PathVariable Long cartId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        try {
            if (quantity <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Quantity must be greater than 0", null));
            }
            cartItemService.updateItemQuantity(cartId, productId, quantity);
            return ResponseEntity.ok(new ApiResponse("Quantity updated successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Cart or Product not found", null));
        } catch (Exception e) {
            log.error("Error updating item quantity. CartId: {}, ProductId: {}", cartId, productId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(INTERNAL_SERVER_ERROR, null));
        }
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<ApiResponse> removeItemFromCart(
            @PathVariable Long cartId,
            @PathVariable Long productId) {
        try {
            cartItemService.removeItemFromCart(cartId, productId);
            ApiResponse response = new ApiResponse("Item removed from cart successfully", null);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Cart or Product not found", null));
        } catch (Exception e) {
            log.error("Error removing item from cart. CartId: {}, ProductId: {}", cartId, productId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(INTERNAL_SERVER_ERROR, null));
        }
    }
}
