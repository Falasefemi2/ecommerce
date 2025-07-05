package com.femmie.ecommerce.service.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.femmie.ecommerce.enums.OrderStatus;
import com.femmie.ecommerce.exception.ResourceNotFoundException;
import com.femmie.ecommerce.model.Cart;
import com.femmie.ecommerce.model.Order;
import com.femmie.ecommerce.model.OrderItem;
import com.femmie.ecommerce.repository.OrderRepository;
import com.femmie.ecommerce.service.cart.ICartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final ICartService cartService;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public Order placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty. Cannot place order.");
        }

        Order order = createOrder(cart);
        List<OrderItem> orderItemsList = createOrderItems(order, cart);

        order.setOrderItems(new HashSet<>(orderItemsList));
        order.setTotalAmount(calculateTotalAmount(orderItemsList));

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(cart.getId());
        return savedOrder;
    }

    @Override
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus(OrderStatus.PENDING);
        return order;
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart) {
        return cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getUnitPrice());

                    int newInventory = cartItem.getProduct().getInventory() - cartItem.getQuantity();
                    if (newInventory < 0) {
                        throw new UnsupportedOperationException("Not enough inventory");
                    }
                    cartItem.getProduct().setInventory(newInventory);

                    return orderItem;
                })
                .toList();
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
