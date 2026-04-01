package com.mjengo.config;

import com.mjengo.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

/**
 * Global model attributes injected into every template.
 * Provides notification data for the navbar across all pages.
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final NotificationService notificationService;

    public GlobalModelAdvice(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ModelAttribute
    public void addNotificationData(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail != null) {
            model.addAttribute("unreadCount", notificationService.countUnreadFor(userEmail));
            model.addAttribute("recentNotifications", notificationService.getRecentFor(userEmail));
        }
    }
}
