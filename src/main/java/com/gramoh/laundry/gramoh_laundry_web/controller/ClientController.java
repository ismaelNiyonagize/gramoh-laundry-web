package com.gramoh.laundry.gramoh_laundry_web.controller;

import com.gramoh.laundry.gramoh_laundry_web.model.Client;
import com.gramoh.laundry.gramoh_laundry_web.model.Order;
import com.gramoh.laundry.gramoh_laundry_web.model.User;
import com.gramoh.laundry.gramoh_laundry_web.model.Package;
import com.gramoh.laundry.gramoh_laundry_web.service.ClientService;
import com.gramoh.laundry.gramoh_laundry_web.service.OrderService;
import com.gramoh.laundry.gramoh_laundry_web.repository.PackageRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;
    private final OrderService orderService;
    private final PackageRepository packageRepository;

    public ClientController(ClientService clientService,
                            OrderService orderService,
                            PackageRepository packageRepository) {
        this.clientService = clientService;
        this.orderService = orderService;
        this.packageRepository = packageRepository;
    }

    /** Show client registration or update form */
    @GetMapping("/form")
    public String showClientForm(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        clientService.getClientByUser(loggedInUser).ifPresentOrElse(
                existingClient -> model.addAttribute("client", existingClient),
                () -> model.addAttribute("client", new Client())
        );

        List<Package> packages = packageRepository.findAll();
        model.addAttribute("packages", packages);

        return "client-form";
    }

    /** Save client info including selected package */
    @PostMapping("/save")
    public String saveClient(@ModelAttribute("client") Client client, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        // Fetch the selected Package entity
        if (client.getSubscribedPackage() != null && client.getSubscribedPackage().getId() != null) {
            Optional<Package> pkgOpt = packageRepository.findById(client.getSubscribedPackage().getId());
            pkgOpt.ifPresent(client::setSubscribedPackage);
        }

        clientService.saveClient(client, loggedInUser);
        return "redirect:/client/dashboard";
    }

    /** Client dashboard showing orders */
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        Client client = clientService.getClientByUser(loggedInUser).orElse(null);
        if (client == null) return "redirect:/client/form";

        List<Order> orders = orderService.getOrdersForClient(client);
        model.addAttribute("client", client);
        model.addAttribute("orders", orders);

        return "client-dashboard";
    }

}
