package com.gramoh.laundry.gramoh_laundry_web.controller;

import com.gramoh.laundry.gramoh_laundry_web.model.User;
import com.gramoh.laundry.gramoh_laundry_web.service.ClientService;
import com.gramoh.laundry.gramoh_laundry_web.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserService userService;
    private final ClientService clientService;

    public UserController(UserService userService, ClientService clientService) {
        this.userService = userService;
        this.clientService = clientService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "This email is already in use.");
            model.addAttribute("user", user);
            return "register";
        }

        userService.register(user);
        return "login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session) {
        // If user is already logged in, redirect them to dashboard
        if (session.getAttribute("loggedInUser") != null) {
            User user = (User) session.getAttribute("loggedInUser");
            if (user.getRole().name().equals("ADMIN")) {
                return "redirect:/admin";
            } else {
                boolean hasClient = clientService.getClientByUser(user).isPresent();
                return hasClient ? "redirect:/client/dashboard" : "redirect:/client/form";
            }
        }

        model.addAttribute("error", null);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        HttpServletResponse response,
                        @RequestParam(required = false, defaultValue = "false") boolean rememberMe,
                        Model model) {

        // Prevent login if session already has user
        if (session.getAttribute("loggedInUser") != null) {
            User existingUser = (User) session.getAttribute("loggedInUser");
            if (existingUser.getRole().name().equals("ADMIN")) {
                return "redirect:/admin";
            } else {
                boolean hasClient = clientService.getClientByUser(existingUser).isPresent();
                return hasClient ? "redirect:/client/dashboard" : "redirect:/client/form";
            }
        }

        User user = userService.login(username, password);
        System.out.println("💡 Login called: username=" + username);

        if (user != null) {
            // Save user in session only once
            session.setAttribute("loggedInUser", user);
            System.out.println("💡 Session ID after set: " + session.getId());
            System.out.println("💡 User saved in session: " + session.getAttribute("loggedInUser"));

            // Remember me cookie
            if (rememberMe) {
                String token = userService.createRememberMeToken(user);
                Cookie cookie = new Cookie("GRAMOH_REMEMBER", token);
                cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            // Redirect by role
            if (user.getRole().name().equals("ADMIN")) {
                return "redirect:/admin";
            } else {
                boolean hasClient = clientService.getClientByUser(user).isPresent();
                return hasClient ? "redirect:/client/dashboard" : "redirect:/client/form";
            }
        }

        model.addAttribute("error", "Invalid username or password");
        return "login";
    }




    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {

        // Remove session user
        session.invalidate();

        // Delete remember-me cookie if exists
        Cookie cookie = new Cookie("GRAMOH_REMEMBER", "");
        cookie.setMaxAge(0);   // delete immediately
        cookie.setPath("/");
        response.addCookie(cookie);

        // Redirect to home or login page
        return "redirect:/login";
    }

}
