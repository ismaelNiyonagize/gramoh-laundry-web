package com.gramoh.laundry.gramoh_laundry_web.repository;

import com.gramoh.laundry.gramoh_laundry_web.model.Clothes;
import com.gramoh.laundry.gramoh_laundry_web.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClothesRepository extends JpaRepository<Clothes , Long> {

    // Get all clothes for a specific order
    List<Clothes> findByOrder(Order order);

}
