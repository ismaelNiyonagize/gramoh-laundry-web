package com.gramoh.laundry.gramoh_laundry_web.controller;

import com.gramoh.laundry.gramoh_laundry_web.model.*;
import com.gramoh.laundry.gramoh_laundry_web.service.ClothesService;
import com.gramoh.laundry.gramoh_laundry_web.service.EmailService;
import com.gramoh.laundry.gramoh_laundry_web.service.GarmentTypeService;
import com.gramoh.laundry.gramoh_laundry_web.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final OrderService orderService;
    private final ClothesService clothesService;
    private final EmailService emailService;
    private final GarmentTypeService garmentTypeService;

    public AdminController(OrderService orderService,
                           ClothesService clothesService,
                           EmailService emailService,
                           GarmentTypeService garmentTypeService) {
        this.orderService = orderService;
        this.clothesService = clothesService;
        this.emailService = emailService;
        this.garmentTypeService = garmentTypeService;
    }

    @GetMapping
    public String adminHome(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin-dashboard";
    }

    @GetMapping("/orders")
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin-order-list";
    }



    @GetMapping("/order/{orderId}/clothes")
    public String showAddClothesForm(@PathVariable Long orderId, Model model) {
      /*  model.addAttribute("orderId", orderId);
        List<GarmentType> garments = garmentTypeService.getAll();
        model.addAttribute("garments", garments);
        return "clothes-form"; */


        // Load order (for header details and clothes list)
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<GarmentType> garments = garmentTypeService.getAll();

        // Add model attributes
        model.addAttribute("order", order);              // full order object
        model.addAttribute("orderId", orderId);          // needed for form action
        model.addAttribute("garments", garments);
        model.addAttribute("clothesList", order.getClothes());

        return "clothes-form";
    }


  //   add one clothes at time


    @PostMapping("/order/{orderId}/clothes")
    public String addSingleCloth(
            @PathVariable Long orderId,
            @RequestParam("garmentId") Long garmentId,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("notes") String notes,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) throws IOException {

        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Save the new cloth item
        clothesService.addClothesToOrder(orderId, garmentId, quantity, notes, imageFile);

        // Reload order from DB to check updated clothes count
        order = orderService.getOrderById(orderId).orElseThrow();

        // If this is the first item -> change status and send email
        if (order.getClothes().size() == 1) {
            orderService.updateOrderStatus(order, OrderStatus.AWAITING_APPROVAL);

            try {
                String clientEmail = order.getClient().getUser().getUsername();
                String subject = "Your clothes have been added – Please confirm";

                String htmlContent = "<p>Dear Customer,</p>"
                        + "<p>Your clothes have been added for Order #" + order.getId() + ".</p>"
                        + "<p>Please review and confirm your order details.</p>"
                        + "<p><a href='http://localhost:8080/order/list' style='color:blue;font-weight:bold;'>Click here to approve</a></p>"
                        + "<br><p>Thank you!</p>";

                emailService.sendHtmlEmail(clientEmail, subject, htmlContent);
            } catch (Exception e) {
                e.printStackTrace(); // Email failure should NOT stop saving
            }
        }

        // Redirect back to the same page
        return "redirect:/admin/order/" + orderId + "/clothes";
    }



    @PostMapping("/order/{orderId}/ready")
    public String markOrderReady(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId).orElse(null);
        if (order != null && order.getStatus() == OrderStatus.WASHING) {
            orderService.updateOrderStatus(order, OrderStatus.READY);
        }

        String clientEmail = order.getClient().getUser().getUsername();
        String subject = "Your laundry is ready – Please select delivery slot";
        String htmlContent = "<p>Dear our loved and valuable customer,</p>" +
                "<p>Your laundry order #" + order.getId() + " is now ready.</p>" +
                "<p>Please <a href='http://localhost:8080/order/list'>click here</a> to select your preferred delivery slot.</p>" +
                "<p>We look forward to delivering your clothes!</p>";

        emailService.sendHtmlEmail(clientEmail, subject, htmlContent);
        return "redirect:/admin/orders";
    }

    @GetMapping("/order/{orderId}/timeslot")
    public String viewDeliveryTimeSlot(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId).orElse(null);
        if (order == null) {
            model.addAttribute("error", "Order not found.");
            return "error-page";
        }
        model.addAttribute("order", order);
        model.addAttribute("deliverySlot", order.getDeliverySlot());
        return "admin-view-timeslot";
    }

    @GetMapping("/orders/verify")
    public String showAllOrdersForVerification(Model model) {
        List<Order> orders = orderService.getOrdersByStatus(
                List.of(OrderStatus.WASHING, OrderStatus.AWAITING_APPROVAL)
        );

        orders.forEach(order -> {
            if (order.getClient() == null) {
                Client defaultClient = new Client();
                defaultClient.setFullName("N/A");
                order.setClient(defaultClient);
            }
        });

        model.addAttribute("orders", orders);
        return "admin-orders-list-verification";
    }

    @PostMapping("/orders/{orderId}/verify")
    public String verifyOrder(@RequestParam("orderId") Long orderId, Model model) {
        Optional<Order> optionalOrder = orderService.getOrderById(orderId);
        if (optionalOrder.isEmpty()) {
            model.addAttribute("errorMessage", "Order not found for verification.");
            return "redirect:/admin/orders/verify";
        }

        Order order = optionalOrder.get();
        model.addAttribute("order", order);
        model.addAttribute("clothesList", order.getClothes());
        return "admin-order-clothes-verification";
    }

    @PostMapping("/orders/confirm-verification")
    public String confirmVerification(@RequestParam Long orderId) {
        Optional<Order> optionalOrder = orderService.getOrderById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(OrderStatus.VERIFIED);
            orderService.saveOrder(order);
        }
        return "redirect:/admin/orders/verify";
    }

 // admin printing the reports

    @GetMapping("/reports")
    public String reportsPage(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "reports";  // template file name
    }





}
