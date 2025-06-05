package com.notifications.webinterface.controller;

import com.notifications.webinterface.dto.NotificationDto;
import com.notifications.webinterface.dto.UserRegistrationDto;
import com.notifications.webinterface.service.WebInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class HomeController {
    
    @Autowired
    private WebInterfaceService webInterfaceService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto user,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            ResponseEntity<String> response = webInterfaceService.registerUser(user);
            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("success", "User registered successfully!");
                webInterfaceService.logUserAction("system", "USER_REGISTRATION", 
                    "New user registered: " + user.getEmail());
                return "redirect:/dashboard";
            } else {
                redirectAttributes.addFlashAttribute("error", "Registration failed: " + response.getBody());
                return "redirect:/register";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("logs", webInterfaceService.getRecentLogs(10));
        return "dashboard";
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        model.addAttribute("notification", new NotificationDto());
        return "notifications";
    }

    @PostMapping("/notifications/send")
    public String sendNotification(@Valid @ModelAttribute("notification") NotificationDto notification,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "notifications";
        }

        try {
            webInterfaceService.sendNotification(notification);
            redirectAttributes.addFlashAttribute("success", "Notification sent successfully!");
            webInterfaceService.logUserAction("admin", "NOTIFICATION_SENT", 
                "Notification sent to: " + notification.getRecipient());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send notification: " + e.getMessage());
        }

        return "redirect:/notifications";
    }

    @GetMapping("/monitoring")
    public String monitoring() {
        return "monitoring";
    }

    @GetMapping("/api/logs")
    @ResponseBody
    public ResponseEntity<?> getLogs() {
        return ResponseEntity.ok(webInterfaceService.getRecentLogs(50));
    }
}
