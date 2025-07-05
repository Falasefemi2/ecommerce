package com.femmie.ecommerce.service.order;

import java.util.List;

import com.femmie.ecommerce.model.Order;

public interface IOrderService {

    Order placeOrder(Long userId);

    Order getOrder(Long orderId);

    List<Order> getUserOrders(Long userId);
}
