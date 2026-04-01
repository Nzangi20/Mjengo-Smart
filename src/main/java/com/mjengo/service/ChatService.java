package com.mjengo.service;

import com.mjengo.model.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory chat message store with project-based threads.
 */
@Service
public class ChatService {

    private final List<ChatMessage> messages = new ArrayList<>();

    public void send(ChatMessage message) {
        messages.add(message);
    }

    public List<ChatMessage> getByProject(String projectId) {
        return messages.stream()
                .filter(m -> m.getProjectId() != null && m.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }

    /** Same storage key as {@link #getByProject}; supports both project ids and {@code DM:...} keys. */
    public List<ChatMessage> getByChannelKey(String channelKey) {
        return getByProject(channelKey);
    }

    public List<ChatMessage> getAll() {
        return messages;
    }

    /**
     * Get distinct project IDs that have messages.
     */
    public List<String> getActiveProjectIds() {
        return messages.stream()
                .map(ChatMessage::getProjectId)
                .filter(id -> id != null && !id.startsWith("DM:"))
                .distinct()
                .collect(Collectors.toList());
    }
}
