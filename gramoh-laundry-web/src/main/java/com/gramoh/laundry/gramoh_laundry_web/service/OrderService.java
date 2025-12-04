package com.gramoh.laundry.gramoh_laundry_web.service;

import com.gramoh.laundry.gramoh_laundry_web.model.*;
import com.gramoh.laundry.gramoh_laundry_web.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Create a new order for a client.
     * Weight limit is validated before saving.
     */
    public Order createOrder(Client client, String serviceType) {
        Order order = new Order();
        order.setClient(client);
        order.setServiceType(serviceType);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        // weight check before saving
        validateWeightLimit(order);

        return orderRepository.save(order);
    }

    /**
     * Validate client weight limit before saving any order
     */
    private void validateWeightLimit(Order order) {
        if (order.getClothes() == null || order.getClothes().isEmpty()) {
            // skip validation if no clothes yet
            return;
        }

        double currentOrderWeight = calculateOrderWeight(order);
        double monthlyUsed = calculateClientMonthlyWeight(order.getClient().getId());
        double limit = order.getClient().getSubscribedPackage().getWeightLimit();
        double remaining = limit - monthlyUsed;

        if (currentOrderWeight > remaining) {
            throw new IllegalArgumentException("Client exceeded monthly weight limit by " +
                    (currentOrderWeight - remaining) + " kg");
        }
    }


    /**
     * Get an order by ID.
     */
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Update the order status.
     */
    public Order updateOrderStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        return orderRepository.save(order);
    }

    /**
     * Update delivery slot.
     */
    public Order updateDeliverySlot(Order order, String slot, LocalDate date) {
        order.setDeliverySlot(slot);
        order.setDeliveryDate(date);
        return orderRepository.save(order);
    }

    /**
     * Get all orders in the system.
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersNeedingVerification() {
        return orderRepository.findByStatusIn(
                List.of(OrderStatus.WASHING, OrderStatus.AWAITING_APPROVAL)
        );
    }


    /**
     * Get all orders of a specific client
     */
    public List<Order> getOrdersForClient(Client client) {
        return orderRepository.findByClient(client);
    }


    public List<Order> getOrdersByStatus(List<OrderStatus> statuses) {
        return orderRepository.findByStatusIn(statuses);
    }

    /**
     * Save order with weight validation.
     */
    public Order saveOrder(Order order) {
        validateWeightLimit(order);
        return orderRepository.save(order);
    }

    /**
     * Calculate total used weight per month.
     */
    public double getTotalUsedWeightForMonth(Long clientId, int year, int month) {
        List<Order> orders = orderRepository.findByClientIdAndMonth(clientId, year, month);
        return orders.stream()
                .mapToDouble(Order::getTotalWeight)
                .sum();
    }

    /**
     * 1️⃣ Calculate weight of a single order.
     */
    private double calculateOrderWeight(Order order) {
        if (order.getClothes() == null || order.getClothes().isEmpty()) return 0.0;
        return order.getClothes().stream()
                .mapToDouble(Clothes::getWeight)
                .sum();
    }


    /**
     * 2️⃣ Calculate total weight used by a client this month.
     */
    public double calculateClientMonthlyWeight(Long clientId) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<Order> orders = orderRepository.findByClientId(clientId);

        return orders.stream()
                .filter(order -> !order.getOrderDate().toLocalDate().isBefore(startOfMonth)
                        && !order.getOrderDate().toLocalDate().isAfter(endOfMonth))
                .flatMap(order -> order.getClothes().stream())
                .mapToDouble(Clothes::getWeight)  // ✅ use the stored weight
                .sum();
    }

}
