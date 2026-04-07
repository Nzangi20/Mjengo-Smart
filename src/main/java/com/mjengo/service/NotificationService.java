package com.mjengo.service;

import com.mjengo.model.Notification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory notification service.
 * Handles creating, reading, and marking notifications as read.
 */
@Service
public class NotificationService {

    private final List<Notification> notifications = new ArrayList<>();
    private final PersistentStoreService store;

    public NotificationService(PersistentStoreService store) {
        this.store = store;
        notifications.addAll(store.loadList("notifications", Notification.class));
        Notification.syncIdGenerator(notifications.stream().map(Notification::getId).max(Long::compareTo).orElse(0L) + 1L);
    }

    /**
     * Create a notification for a user.
     */
    public void notify(String userEmail, String type, String message, String link) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        notifications.add(new Notification(userEmail, type, message, link, ts));
        save();
    }

    /**
     * Create a broadcast notification for all users.
     */
    public void broadcast(String type, String message, String link) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        notifications.add(new Notification("ALL", type, message, link, ts));
        save();
    }

    /**
     * Get unread notifications for a user (includes broadcasts).
     */
    public List<Notification> getUnreadFor(String userEmail) {
        return notifications.stream()
                .filter(n -> !n.isRead() && ("ALL".equals(n.getUserId()) || n.getUserId().equals(userEmail)))
                .collect(Collectors.toList());
    }

    /**
     * Get recent notifications for a user (last 10).
     */
    public List<Notification> getRecentFor(String userEmail) {
        List<Notification> userNotifs = notifications.stream()
                .filter(n -> "ALL".equals(n.getUserId()) || n.getUserId().equals(userEmail))
                .collect(Collectors.toList());
        int size = userNotifs.size();
        return userNotifs.subList(Math.max(0, size - 10), size);
    }

    /**
     * Count unread notifications for a user.
     */
    public int countUnreadFor(String userEmail) {
        return (int) notifications.stream()
                .filter(n -> !n.isRead() && ("ALL".equals(n.getUserId()) || n.getUserId().equals(userEmail)))
                .count();
    }

    /**
     * Mark a notification as read.
     */
    public void markRead(long notifId) {
        notifications.stream()
                .filter(n -> n.getId() == notifId)
                .findFirst()
                .ifPresent(n -> {
                    n.setRead(true);
                    save();
                });
    }

    /**
     * Mark all notifications for a user as read.
     */
    public void markAllRead(String userEmail) {
        notifications.stream()
                .filter(n -> "ALL".equals(n.getUserId()) || n.getUserId().equals(userEmail))
                .forEach(n -> n.setRead(true));
        save();
    }

    private void save() {
        store.saveList("notifications", notifications);
    }
}
