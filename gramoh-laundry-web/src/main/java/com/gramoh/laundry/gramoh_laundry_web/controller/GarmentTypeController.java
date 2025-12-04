package com.gramoh.laundry.gramoh_laundry_web.controller;

import com.itextpdf.text.DocumentException;
import com.gramoh.laundry.gramoh_laundry_web.model.GarmentType;
import com.gramoh.laundry.gramoh_laundry_web.service.GarmentTypeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/garments")

public class GarmentTypeController {

    @Autowired
    private GarmentTypeService garmentTypeService;

    /**
     * List all garment types (used for dropdowns or admin pages)
     */
    @GetMapping("/list")
    @ResponseBody
    public List<GarmentType> getAllGarments() {
        return garmentTypeService.getAll();
    }


    // CREATE new garment type
    @PostMapping("/add")
    @ResponseBody
    public GarmentType addGarment(@RequestBody GarmentType type) {
        return garmentTypeService.addType(type);
    }

    // GET garment by ID
    @GetMapping("/{id}")
    @ResponseBody
    public GarmentType getGarment(@PathVariable Long id) {
        return garmentTypeService.getGarmentById(id)
                .orElseThrow(() -> new RuntimeException("Garment not found: " + id));
    }

    // UPDATE garment type
    @PutMapping("/update/{id}")
    @ResponseBody
    public GarmentType updateGarment(@PathVariable Long id, @RequestBody GarmentType type) {
        return garmentTypeService.updateGarment(id, type);
    }

    // DELETE garment type
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String deleteGarment(@PathVariable Long id) {
        garmentTypeService.deleteGarment(id);
        return "Deleted successfully";
    }


    @GetMapping("/admin")
    public String garmentAdminPage() {
        return "garment-types";
    }
}
