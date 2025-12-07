package com.gramoh.laundry.gramoh_laundry_web.service;



import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageUploadService {

    @Autowired
    private Cloudinary cloudinary;





    /**
     * Upload image to Cloudinary with public access and order-based folder
     */
    public String upload(MultipartFile file, Long orderId) throws IOException {

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "gramoh/order_" + orderId,
                        "resource_type", "image",
                        "type", "upload",
                        "access_mode", "public"
                )
        );

        return uploadResult.get("secure_url").toString();
    }
}
