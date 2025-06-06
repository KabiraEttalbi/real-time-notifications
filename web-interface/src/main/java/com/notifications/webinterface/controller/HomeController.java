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
    public String home(Model model) {
        model.addAttribute("title", "Accueil - Notifications Platform");
        model.addAttribute("content", "index");
        model.addAttribute("scripts", "");
        return "layout";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        model.addAttribute("title", "Inscription - Notifications Platform");
        model.addAttribute("content", "register");
        model.addAttribute("scripts", "");
        return "layout";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto user,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Inscription - Notifications Platform");
            model.addAttribute("content", "register");
            model.addAttribute("scripts", "");
            return "layout";
        }

        try {
            ResponseEntity<String> response = webInterfaceService.registerUser(user);
            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("success", "Utilisateur inscrit avec succès!");
                webInterfaceService.logUserAction("system", "USER_REGISTRATION", 
                    "New user registered: " + user.getEmail());
                return "redirect:/dashboard";
            } else {
                redirectAttributes.addFlashAttribute("error", "Échec de l'inscription: " + response.getBody());
                return "redirect:/register";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Échec de l'inscription: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("logs", webInterfaceService.getRecentLogs(10));
        model.addAttribute("title", "Dashboard - Notifications Platform");
        model.addAttribute("content", "dashboard");
        model.addAttribute("scripts", "dashboard-scripts");
        return "layout";
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        model.addAttribute("notification", new NotificationDto());
        model.addAttribute("title", "Notifications - Notifications Platform");
        model.addAttribute("content", "notifications");
        model.addAttribute("scripts", "notifications-scripts");
        return "layout";
    }

    @PostMapping("/notifications/send")
    public String sendNotification(@Valid @ModelAttribute("notification") NotificationDto notification,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Notifications - Notifications Platform");
            model.addAttribute("content", "notifications");
            model.addAttribute("scripts", "notifications-scripts");
            return "layout";
        }

        try {
            webInterfaceService.sendNotification(notification);
            redirectAttributes.addFlashAttribute("success", "Notification envoyée avec succès!");
            webInterfaceService.logUserAction("admin", "NOTIFICATION_SENT", 
                "Notification sent to: " + notification.getRecipient());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Échec de l'envoi: " + e.getMessage());
        }

        return "redirect:/notifications";
    }

    @GetMapping("/monitoring")
    public String monitoring(Model model) {
        model.addAttribute("title", "Monitoring - Notifications Platform");
        model.addAttribute("content", "monitoring");
        model.addAttribute("scripts", "monitoring-scripts");
        return "layout";
    }

    @GetMapping("/api/logs")
    @ResponseBody
    public ResponseEntity<?> getLogs() {
        return ResponseEntity.ok(webInterfaceService.getRecentLogs(50));
    }
}
