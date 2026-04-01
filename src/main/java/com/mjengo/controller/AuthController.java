package com.mjengo.controller;

import com.mjengo.model.User;
import com.mjengo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles user login, registration, and logout flows.
 * Uses HttpSession to persist the logged-in user across requests.
 */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ---- LOGIN ----

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // If already logged in, redirect to dashboard
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        var userOpt = userService.authenticate(email, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("user", user);
            session.setAttribute("userName", user.getFullName());
            session.setAttribute("userRole", user.getRole());
            session.setAttribute("userEmail", user.getEmail());
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password. Please try again.");
            return "redirect:/login";
        }
    }

    // ---- REGISTER ----

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User newUser = new User(fullName, email, password, role.toUpperCase());
        boolean success = userService.register(newUser);
        if (success) {
            // Log them in immediately after registration
            session.setAttribute("user", newUser);
            session.setAttribute("userName", newUser.getFullName());
            session.setAttribute("userRole", newUser.getRole());
            session.setAttribute("userEmail", newUser.getEmail());
            redirectAttributes.addFlashAttribute("success", "Account created successfully! Welcome to Mjengo.");
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "An account with that email already exists.");
            return "redirect:/register";
        }
    }

    // ---- LOGOUT ----

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
