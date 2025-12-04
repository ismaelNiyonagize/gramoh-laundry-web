package com.gramoh.laundry.gramoh_laundry_web.controller;

import com.gramoh.laundry.gramoh_laundry_web.model.*;
import com.gramoh.laundry.gramoh_laundry_web.repository.GarmentTypeRepository;
import com.gramoh.laundry.gramoh_laundry_web.service.ClientService;
import com.gramoh.laundry.gramoh_laundry_web.service.GarmentTypeService;
import com.gramoh.laundry.gramoh_laundry_web.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Handles client orders: create, view, select delivery slot, confirm clothes, confirm delivery
 */
@Controller
@RequestMapping("/order")
public class OrderController {



   private final OrderService orderService;
   private final ClientService clientService;
    private final GarmentTypeService garmentTypeService;


    private GarmentTypeRepository garmentTypeRepository;


/*
   public OrderController(OrderService orderService, ClientService clientService, GarmentTypeRepository garmentTypeRepository) {
       this.orderService = orderService;
       this.clientService = clientService;
       this.garmentTypeRepository = garmentTypeRepository;
   }
     */



    public OrderController(OrderService orderService,
                           ClientService clientService,
                           GarmentTypeService garmentTypeService,
                           GarmentTypeRepository garmentTypeRepository) {
        this.orderService = orderService;
        this.clientService = clientService;
        this.garmentTypeService = garmentTypeService;
        this.garmentTypeRepository = garmentTypeRepository;
    }




    /**
    * Show all orders for the logged-in client
    */
    @GetMapping("/list")
    public String listOrders(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        Client client = clientService.getClientByUser(user).orElse(null);

        if (client == null) return "redirect:/client/form";

        List<Order> orders = orderService.getOrdersForClient(client);
        model.addAttribute("orders", orders);
        return "order-list"; // Thymeleaf page to show orders
    }

    /**
     * Show form to create a new order
     */
    @GetMapping("/new")
    public String showCreateOrderForm(Model model) {
        model.addAttribute("order", new Order());
        return "order-form"; // Thymeleaf page for new order
    }

    /**
     * Create a new order for the logged-in client
     */
    @PostMapping("/save")
    public String createOrder(@ModelAttribute Order order, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        Client client = clientService.getClientByUser(user).orElse(null);

        if (client == null) return "redirect:/client/form";

        orderService.createOrder(client, order.getServiceType());
        return "redirect:/order/list";
    }

    /**
     * Show clothes for client to confirm
     */
    @GetMapping("/{orderId}/confirm-clothes")
    public String showClothesConfirmation(@PathVariable Long orderId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        Client client = clientService.getClientByUser(user).orElse(null);

        if (client == null) return "redirect:/client/form";

        Order order = orderService.getOrderById(orderId).orElse(null);
        if (order == null || !order.getClient().equals(client)) return "redirect:/order/list";

        model.addAttribute("order", order);
        model.addAttribute("clothes", order.getClothes());

        // Fetch all garment types from database
        List<GarmentType> garments = garmentTypeService.getAll();

        model.addAttribute("garments", garments);




        return "confirm-clothes"; // Thymeleaf page to show clothes and confirm
    }

    /**
     * Handle client confirmation of clothes
     */
    @PostMapping("/{orderId}/confirm-clothes")
    public String confirmClothes(@PathVariable Long orderId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        Client client = clientService.getClientByUser(user).orElse(null);

        if (client == null) return "redirect:/client/form";

        Order order = orderService.getOrderById(orderId).orElse(null);
        if (order != null && order.getClient().equals(client)) {
            orderService.updateOrderStatus(order, OrderStatus.WASHING);
        }

        return "redirect:/order/list";
    }

    /**
     * Show delivery slot form only for READY orders
     */
    @GetMapping("/{orderId}/delivery-slot")
    public String showDeliverySlotForm(@PathVariable Long orderId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        Client client = clientService.getClientByUser(user).orElse(null);

        Order order = orderService.getOrderById(orderId).orElse(null);
        if (order == null || client == null || !order.getClient().equals(client) || order.getStatus() != OrderStatus.READY) {
            return "redirect:/order/list";
        }

        model.addAttribute("orderId", orderId);
        return "delivery-slot-form"; // Thymeleaf page to pick slot
    }

    /**
     * Save chosen delivery slot
     */
    @PostMapping("/{orderId}/delivery-slot")
    public String saveDeliverySlot(@PathVariable Long orderId,
                                   @RequestParam String slot,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                   HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        Client client = clientService.getClientByUser(user).orElse(null);

        Order order = orderService.getOrderById(orderId).orElse(null);
        if (order != null && client != null && order.getClient().equals(client) && order.getStatus() == OrderStatus.READY) {
            orderService.updateDeliverySlot(order, slot ,date);
        }

        return "redirect:/order/list";
    }

    /**
     * Client confirms they received clothes → order DELIVERED
     */
    @PostMapping("/{orderId}/confirm-delivery")
    public String confirmDelivery(@PathVariable Long orderId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        Client client = clientService.getClientByUser(user).orElse(null);

        Order order = orderService.getOrderById(orderId).orElse(null);
        if (order != null && client != null && order.getClient().equals(client) && order.getStatus() == OrderStatus.READY) {
            orderService.updateOrderStatus(order, OrderStatus.DELIVERED);
        }

        return "redirect:/order/list";
    }
}
