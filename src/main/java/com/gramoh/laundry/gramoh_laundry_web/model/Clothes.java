package com.gramoh.laundry.gramoh_laundry_web.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a single piece of clothing linked to an Order.
 */
@Entity
@Table(name = "clothes")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Clothes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Type of clothing, e.g., Shirt, Pants
    private String type;

    // Quantity of this type
    private Integer quantity;

    // Optional notes (e.g., stain, delicate)
    private String notes;

    // URL/path of uploaded picture of the clothing
    private String imageUrl;

    // Many clothes items belong to one order
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;


    // Link to garment type (Shirt, Pants, etc.)
    @ManyToOne
    @JoinColumn(name = "garment_type_id", nullable = false)
    private GarmentType garmentType;

    // ✅ Total weight for this clothes record
    @Column(nullable = false)
    private double weight;

}
