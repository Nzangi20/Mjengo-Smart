package com.mjengo.controller;

import com.mjengo.dto.ChatMessageDto;
import com.mjengo.model.ChatMessage;
import com.mjengo.model.User;
import com.mjengo.service.ChatService;
import com.mjengo.service.NotificationService;
import com.mjengo.service.ProjectService;
import com.mjengo.service.UserService;
import com.mjengo.util.ChatRouting;
import jakarta.servlet.http.HttpSession;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project-wide channels and direct (1:1) messaging with WebSocket push.
 */
@Controller
public class ChatController {

    private final ChatService chatService;
    private final NotificationService notificationService;
    private final ProjectService projectService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, NotificationService notificationService,
            ProjectService projectService, UserService userService,
            SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.notificationService = notificationService;
        this.projectService = projectService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/messages")
    public String messagesPage(@RequestParam(value = "project", required = false) String projectId,
            @RequestParam(value = "dm", required = false) Long peerUserId,
            HttpSession session, Model model) {
        String role = (String) session.getAttribute("userRole");
        String email = (String) session.getAttribute("userEmail");
        model.addAttribute("title", "Messages");
        model.addAttribute("activeModule", "messages");
        if ("CLIENT".equals(role)) {
            model.addAttribute("projects", projectService.findByClientEmail(email));
        } else {
            model.addAttribute("projects", projectService.getAll());
        }
        model.addAttribute("activeProjectIds", chatService.getActiveProjectIds());

        List<User> others = userService.getAllUsers().stream()
                .filter(u -> email == null || !email.equalsIgnoreCase(u.getEmail()))
                .collect(Collectors.toList());
        model.addAttribute("directoryUsers", others);

        if (StringUtils.hasText(projectId)) {
            if ("CLIENT".equals(role) && email != null) {
                try {
                    long pid = Long.parseLong(projectId.trim());
                    if (!projectService.clientOwnsProject(pid, email)) {
                        return "redirect:/messages";
                    }
                } catch (NumberFormatException e) {
                    return "redirect:/messages";
                }
            }
            String key = ChatRouting.projectChannelKey(projectId);
            model.addAttribute("chatMode", "PROJECT");
            model.addAttribute("selectedProject", key);
            model.addAttribute("selectedPeer", null);
            model.addAttribute("channelKey", key);
            model.addAttribute("wsDestination", ChatRouting.stompDestination(key));
            model.addAttribute("messages", chatService.getByChannelKey(key));
        } else if (peerUserId != null && peerUserId > 0) {
            User peer = userService.findById(peerUserId).orElse(null);
            if (peer == null || email == null || email.equalsIgnoreCase(peer.getEmail())) {
                return "redirect:/messages";
            }
            String key = ChatRouting.directChannelKey(email, peer.getEmail());
            if (key.isEmpty()) {
                return "redirect:/messages";
            }
            model.addAttribute("chatMode", "DIRECT");
            model.addAttribute("selectedProject", null);
            model.addAttribute("selectedPeer", peer);
            model.addAttribute("channelKey", key);
            model.addAttribute("wsDestination", ChatRouting.stompDestination(key));
            model.addAttribute("messages", chatService.getByChannelKey(key));
        }

        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail != null) {
            model.addAttribute("unreadCount", notificationService.countUnreadFor(userEmail));
            model.addAttribute("recentNotifications", notificationService.getRecentFor(userEmail));
        }

        return "messages";
    }

    @PostMapping("/messages/send")
    public String sendMessage(@RequestParam(value = "chatMode", defaultValue = "PROJECT") String chatMode,
            @RequestParam(value = "projectId", required = false) String projectId,
            @RequestParam(value = "peerUserId", required = false) Long peerUserId,
            @RequestParam("content") String content,
            HttpSession session,
            RedirectAttributes ra) {
        String senderRole = (String) session.getAttribute("userRole");
        String email = (String) session.getAttribute("userEmail");
        String senderName = (String) session.getAttribute("userName");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        if ("DIRECT".equalsIgnoreCase(chatMode)) {
            if (peerUserId == null || email == null) {
                return "redirect:/messages";
            }
            User peer = userService.findById(peerUserId).orElse(null);
            if (peer == null || email.equalsIgnoreCase(peer.getEmail())) {
                return "redirect:/messages";
            }
            String channelKey = ChatRouting.directChannelKey(email, peer.getEmail());
            ChatMessage msg = new ChatMessage(channelKey, senderName, senderRole, content.trim(), timestamp);
            chatService.send(msg);
            String dest = ChatRouting.stompDestination(channelKey);
            messagingTemplate.convertAndSend(dest, ChatMessageDto.from(msg));
            long senderUserId = userService.findByEmail(email).map(User::getId).orElse(0L);
            notificationService.notify(peer.getEmail(), "MESSAGE",
                    senderName + " sent you a direct message",
                    "/messages?dm=" + senderUserId);
            return "redirect:/messages?dm=" + peerUserId;
        }

        if (projectId == null || projectId.isBlank()) {
            return "redirect:/messages";
        }
        if ("CLIENT".equals(senderRole) && email != null) {
            try {
                long pid = Long.parseLong(projectId.trim());
                if (!projectService.clientOwnsProject(pid, email)) {
                    return "redirect:/messages";
                }
            } catch (NumberFormatException e) {
                return "redirect:/messages";
            }
        }

        String key = ChatRouting.projectChannelKey(projectId);
        ChatMessage msg = new ChatMessage(key, senderName, senderRole, content.trim(), timestamp);
        chatService.send(msg);
        messagingTemplate.convertAndSend(ChatRouting.stompDestination(key), ChatMessageDto.from(msg));

        notificationService.broadcast("MESSAGE",
                senderName + " sent a message in project chat",
                "/messages?project=" + key);

        return "redirect:/messages?project=" + key;
    }
}
