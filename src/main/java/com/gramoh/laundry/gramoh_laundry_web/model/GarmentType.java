package com.gramoh.laundry.gramoh_laundry_web.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "garment_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GarmentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Example: "Shirt", "Pants", "Jacket"
    @Column(nullable = false, unique = true)
    private String name;

    // Example: 0.3 kg per item
    @Column(nullable = false)
    private double weightPerItem;
}
