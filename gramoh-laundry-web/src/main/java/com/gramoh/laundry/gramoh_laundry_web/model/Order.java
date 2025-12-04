package com.gramoh.laundry.gramoh_laundry_web.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a laundry order for a client.
 */
@Entity
@Table(name = "orders")
@Data // Generates getters, setters, toString, equals, hashcode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Type of service (e.g., Washing, Ironing)
    private String serviceType;

    // For monthly clients, amount can be null
    private Double amount;

    // Automatically records the time the order was created
    private LocalDateTime orderDate = LocalDateTime.now();

    // Optional delivery date
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    // Order status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    // Delivery slot chosen by client (e.g., "7-9 PM")
    private String deliverySlot;

    // ✅ Total clothes weight for this order (in kilograms)
    @Column(name = "total_weight")
    private Double totalWeight = 0.0;

    // 🔗 Relationship: Many orders belong to one client
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    // 🔗 Relationship: One order can have many clothes
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Clothes> clothes;
}
