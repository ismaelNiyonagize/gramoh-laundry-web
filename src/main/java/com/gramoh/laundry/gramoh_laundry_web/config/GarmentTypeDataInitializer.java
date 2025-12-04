package com.gramoh.laundry.gramoh_laundry_web.config;

import com.gramoh.laundry.gramoh_laundry_web.model.GarmentType;
import com.gramoh.laundry.gramoh_laundry_web.repository.GarmentTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GarmentTypeDataInitializer {

    @Bean
    CommandLineRunner initGarmentTypes(GarmentTypeRepository garmentTypeRepository) {
        return args -> {

            if (garmentTypeRepository.count() == 0) { // Only populate if empty
                List<GarmentType> garmentTypes = List.of(
                        new GarmentType(null, "Shirt / Ishati isanzwe", 0.25),
                        new GarmentType(null, "T-shirt / Tisheti ", 0.15),
                        new GarmentType(null, "Blouse / Buluze", 0.2),
                        new GarmentType(null, "Pants / ITISI", 0.4),
                        new GarmentType(null, "Jeans / Ikoboyi", 0.5),
                        new GarmentType(null, "Jacket / Ijaketi", 0.8),
                        new GarmentType(null, "Coat / Ikoto Rirerire /", 1.0),
                        new GarmentType(null, "Sweater / Umupira wimbeho", 0.6),
                        new GarmentType(null, "Skirt / Ijipo ", 0.3),
                        new GarmentType(null, "Dress / Ikanzu ", 0.35),
                        new GarmentType(null, "Shorts / ikabutura ", 0.2),
                        new GarmentType(null, "Underwear / Umwambaro wimbere ", 0.05),
                        new GarmentType(null, "Socks (pair) / Amasogisi", 0.05),
                        new GarmentType(null, "Towel (small) / Agaselievite ", 0.3),
                        new GarmentType(null, "Towel (medium) / ISUME Moayene ", 0.5),
                        new GarmentType(null, "Towel (large) / ISUME NINI", 0.8),
                        new GarmentType(null, "Bed Sheet (single) / Ishuka ", 0.7),
                        new GarmentType(null, "Bed Sheet (double) / Amashuka", 1.2),
                        new GarmentType(null, "Blanket (thin) / Ubulingiti", 1.5),
                        new GarmentType(null, "Blanket (thick) / Ubulingiti BUnini", 2.5),
                        new GarmentType(null, "Pillowcase / Umwenda Umusego", 0.2),
                        new GarmentType(null, "Pillow / Umusego", 1.0),
                        new GarmentType(null, "Duvet Cover/ Kubureri Moayene", 1.5),
                        new GarmentType(null, "Duvet / Comforter / Kuvreri NINI", 3.0),
                        new GarmentType(null, "Tablecloth/ Umwenda wo kumeza", 0.6),
                        new GarmentType(null, "Curtain (small)/ irido rito ", 0.5),
                        new GarmentType(null, "Curtain (large)/ irido rinini", 1.0)
                );

                garmentTypeRepository.saveAll(garmentTypes);
                System.out.println("Initialized Garment Types.");
            }
        };
    }
}
