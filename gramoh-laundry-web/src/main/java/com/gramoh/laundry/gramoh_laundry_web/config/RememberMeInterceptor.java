package com.gramoh.laundry.gramoh_laundry_web.config;

import com.gramoh.laundry.gramoh_laundry_web.model.User;
import com.gramoh.laundry.gramoh_laundry_web.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RememberMeInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public RememberMeInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);

        // If already logged in, allow the request
        if (session != null && session.getAttribute("loggedInUser") != null) {
            return true;
        }

        // Search remember-me cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("GRAMOH_REMEMBER".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    User user = userService.getByRememberMeToken(token);

                    if (user != null) {
                        // Restore session and continue request
                        request.getSession(true).setAttribute("loggedInUser", user);
                        return true;
                    }
                }
            }
        }

        return true; // continue if no user found
    }
}
