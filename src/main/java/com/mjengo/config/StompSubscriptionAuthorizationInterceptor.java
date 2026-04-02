package com.mjengo.config;

import com.mjengo.service.ProjectService;
import com.mjengo.util.ChatRouting;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Enforces tenant isolation for STOMP subscriptions:
 * - CLIENT users cannot subscribe to other clients' project topics.
 * - CLIENT users cannot subscribe to DM threads they are not part of.
 */
@Component
public class StompSubscriptionAuthorizationInterceptor implements ChannelInterceptor {

    private static final String PROJECT_TOPIC_PREFIX = "/topic/project.";
    private static final String DM_TOPIC_PREFIX = "/topic/dm.";

    private final ProjectService projectService;

    public StompSubscriptionAuthorizationInterceptor(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() != StompCommand.SUBSCRIBE) {
            return message;
        }

        String destination = accessor.getDestination();
        if (destination == null || destination.isBlank()) {
            return message;
        }

        Map<String, Object> sessionAttrs = accessor.getSessionAttributes();
        if (sessionAttrs == null) {
            return message;
        }

        String userRole = (String) sessionAttrs.get("userRole");
        if (!"CLIENT".equals(userRole)) {
            return message; // Staff can subscribe freely.
        }

        String clientEmail = (String) sessionAttrs.get("userEmail");
        if (clientEmail == null || clientEmail.isBlank()) {
            return null; // No authenticated email in session => block.
        }

        // Project channel subscription: /topic/project.{projectId}
        if (destination.startsWith(PROJECT_TOPIC_PREFIX)) {
            String projectIdStr = destination.substring(PROJECT_TOPIC_PREFIX.length()).trim();
            try {
                long projectId = Long.parseLong(projectIdStr);
                return projectService.clientOwnsProject(projectId, clientEmail) ? message : null;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // Direct message subscription: /topic/dm.{base64url(DM:a|b)}
        if (destination.startsWith(DM_TOPIC_PREFIX)) {
            String key = ChatRouting.directChannelKeyFromStompDestination(destination);
            if (key.isBlank() || !key.startsWith("DM:")) {
                return null;
            }

            // key := "DM:{emailA}|{emailB}"
            String participants = key.substring("DM:".length());
            String[] parts = participants.split("\\|", -1);
            if (parts.length != 2) {
                return null;
            }

            String a = parts[0] != null ? parts[0].trim().toLowerCase() : "";
            String b = parts[1] != null ? parts[1].trim().toLowerCase() : "";
            String me = clientEmail.trim().toLowerCase();
            return (me.equals(a) || me.equals(b)) ? message : null;
        }

        // Unknown topic pattern: allow (to avoid breaking unrelated broker topics).
        return message;
    }
}

