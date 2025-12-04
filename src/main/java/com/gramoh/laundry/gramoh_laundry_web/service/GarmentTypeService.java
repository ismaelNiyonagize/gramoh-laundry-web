package com.gramoh.laundry.gramoh_laundry_web.service;

import com.gramoh.laundry.gramoh_laundry_web.model.GarmentType;
import com.gramoh.laundry.gramoh_laundry_web.repository.GarmentTypeRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GarmentTypeService {

    private final GarmentTypeRepository repo;

    public GarmentTypeService(GarmentTypeRepository repo) {
        this.repo = repo;
    }

    public List<GarmentType> getAll() {


        return repo.findAll();
    }

    public GarmentType addType(GarmentType type) {
        if (repo.existsByName(type.getName())) {
            throw new RuntimeException("Garment type already exists: " + type.getName());
        }
        return repo.save(type);
    }

    public void deleteGarment(Long id) {
    }


    public Optional<GarmentType> getGarmentById(Long id) {
        return repo.findById(id);
    }


    public GarmentType updateGarment(Long id, GarmentType updatedType) {
        GarmentType existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Garment type not found: " + id));

        existing.setName(updatedType.getName());
        existing.setWeightPerItem(updatedType.getWeightPerItem());

        return repo.save(existing);
    }

}
