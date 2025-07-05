package com.femmie.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class OrderDto {

    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status;
    private Set<OrderItemDto> orderItems = new HashSet<>();
}
