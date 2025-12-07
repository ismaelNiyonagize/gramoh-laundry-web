package com.gramoh.laundry.gramoh_laundry_web.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gramoh.laundry.gramoh_laundry_web.model.Clothes;
import com.gramoh.laundry.gramoh_laundry_web.model.GarmentType;
import com.gramoh.laundry.gramoh_laundry_web.model.Order;
import com.gramoh.laundry.gramoh_laundry_web.repository.ClothesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ClothesService {


    @Autowired
    private Cloudinary cloudinary;
    private final ClothesRepository clothesRepository;
    private final OrderService orderService;
    private final GarmentTypeService garmentTypeService;

    @Autowired
    private ImageUploadService imageUploadService;

    public ClothesService(ClothesRepository clothesRepository,
                          OrderService orderService,
                          GarmentTypeService garmentTypeService) {
        this.clothesRepository = clothesRepository;
        this.orderService = orderService;
        this.garmentTypeService = garmentTypeService;
    }

    /**
     * Get all clothes for a specific order
     */
    public List<Clothes> getClothesByOrder(Order order) {
        return clothesRepository.findByOrder(order);
    }

    /**
     * Add a clothes item to an order, linking the garment type and handling image upload
     */
    public Clothes addClothesToOrder(Long orderId, Long garmentId, Integer quantity, String notes, MultipartFile imageFile) throws IOException {
        // 1️⃣ Retrieve the order
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // 2️⃣ Retrieve the garment type
        GarmentType garment = garmentTypeService.getGarmentById(garmentId)
                .orElseThrow(() -> new IllegalArgumentException("Garment not found"));

        // 3️⃣ Create new Clothes entity
        Clothes clothes = new Clothes();
        clothes.setOrder(order);
        clothes.setGarmentType(garment);  // ✅ critical for weight computation
        clothes.setQuantity(quantity);
        clothes.setNotes(notes);
        clothes.setWeight(garment.getWeightPerItem() * quantity); // ✅ added line


        // 4️⃣ Handle image upload
//        if (imageFile != null && !imageFile.isEmpty()) {
//
//            Map uploadResult = cloudinary.uploader().upload(
//                    imageFile.getBytes(),
//                    ObjectUtils.asMap("folder", "gramoh/order_" + orderId)
//            );
//
//            String imageUrl = (String) uploadResult.get("secure_url");
//            clothes.setImageUrl(imageUrl);
//        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = imageUploadService.upload(imageFile, orderId);
            clothes.setImageUrl(imageUrl);
        }



        // 5️⃣ Save and return
        return clothesRepository.save(clothes);
    }

}
