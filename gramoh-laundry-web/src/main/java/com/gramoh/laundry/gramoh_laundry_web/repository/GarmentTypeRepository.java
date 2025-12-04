package com.gramoh.laundry.gramoh_laundry_web.repository;

import com.gramoh.laundry.gramoh_laundry_web.model.GarmentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GarmentTypeRepository extends JpaRepository<GarmentType, Long> {
    boolean existsByName(String name);
}
