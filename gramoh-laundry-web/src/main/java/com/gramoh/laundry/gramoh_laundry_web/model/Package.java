package com.gramoh.laundry.gramoh_laundry_web.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "packages")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Package {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;          // e.g. "Silver", "Gold", "Premium"
    private double weightLimit;   // kg per month
    private double price;         // optional (e.g., 25,000 RWF)
}
