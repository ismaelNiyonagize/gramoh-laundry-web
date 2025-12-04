package com.gramoh.laundry.gramoh_laundry_web.repository;

import com.gramoh.laundry.gramoh_laundry_web.model.Client;
import com.gramoh.laundry.gramoh_laundry_web.model.Order;
import com.gramoh.laundry.gramoh_laundry_web.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Get all orders for a given client
    List<Order> findByClient(Client client);


    List<Order> findByClientId(Long clientId);

    // Get all orders with given statuses
    List<Order> findByStatusIn(List<OrderStatus> statuses);

    // ✅ Custom query to get all orders for a client in a specific month/year
    @Query("""
        SELECT o FROM Order o 
        WHERE o.client.id = :clientId 
        AND FUNCTION('YEAR', o.orderDate) = :year 
        AND FUNCTION('MONTH', o.orderDate) = :month
    """)
    List<Order> findByClientIdAndMonth(
            @Param("clientId") Long clientId,
            @Param("year") int year,
            @Param("month") int month
    );
}
