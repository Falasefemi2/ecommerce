package com.femmie.ecommerce.service.order;

import java.util.List;

import com.femmie.ecommerce.dto.OrderDto;
import com.femmie.ecommerce.model.Order;

public interface IOrderService {

    Order placeOrder(Long userId);

    OrderDto getOrder(Long orderId);

    List<OrderDto> getUserOrders(Long userId);
}
