package com.gramoh.laundry.gramoh_laundry_web.config;

import com.gramoh.laundry.gramoh_laundry_web.model.Package;
import com.gramoh.laundry.gramoh_laundry_web.repository.PackageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PackageRepository packageRepository;

    public DataInitializer(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    @Override



        public void run(String... args) throws Exception {
            // Delete existing packages first
            packageRepository.deleteAll();

            // Now add the corrected packages
            Package standard = new Package(null, "Standard Plan", 32, 8000);
            Package confort  = new Package(null, "Confort Plan", 72, 15600);
            Package student  = new Package(null, "Student Plan", 60, 11000);
            Package deluxe   = new Package(null, "Deluxe Plan", 100, 20000);
            Package elite    = new Package(null, "Elite Unlimited", 0, 30000);

            packageRepository.save(standard);
            packageRepository.save(confort);
            packageRepository.save(student);
            packageRepository.save(deluxe);
            packageRepository.save(elite);

            System.out.println("✅ Packages re-populated in database.");
    }
}
