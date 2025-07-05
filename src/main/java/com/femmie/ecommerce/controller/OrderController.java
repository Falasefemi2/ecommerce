package com.femmie.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.femmie.ecommerce.dto.CreateOrderRequest;
import com.femmie.ecommerce.model.Order;
import com.femmie.ecommerce.response.ApiResponse;
import com.femmie.ecommerce.service.order.IOrderService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final IOrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderService.placeOrder(request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Order created successfully", order));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrder(orderId);
        return ResponseEntity.ok(new ApiResponse<>("Order retrieved successfully", order));
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(new ApiResponse<>("Orders retrieved successfully", orders));
    }
}
