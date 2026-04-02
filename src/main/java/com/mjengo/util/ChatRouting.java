package com.mjengo.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Canonical channel keys and STOMP destinations for project-wide vs direct (1:1) chat.
 */
public final class ChatRouting {

    private ChatRouting() {
    }

    public static String projectChannelKey(String numericProjectId) {
        return numericProjectId == null ? "" : numericProjectId.trim();
    }

    /**
     * Stable key for a DM thread so both participants resolve the same channel.
     */
    public static String directChannelKey(String emailA, String emailB) {
        String a = normalizeEmail(emailA);
        String b = normalizeEmail(emailB);
        if (a.isEmpty() || b.isEmpty()) {
            return "";
        }
        String first = a.compareTo(b) <= 0 ? a : b;
        String second = a.compareTo(b) <= 0 ? b : a;
        return "DM:" + first + "|" + second;
    }

    public static boolean isDirectChannel(String channelKey) {
        return channelKey != null && channelKey.startsWith("DM:");
    }

    /**
     * STOMP topic for subscriptions; safe characters only in destination path segment.
     */
    public static String stompDestination(String channelKey) {
        if (channelKey == null || channelKey.isEmpty()) {
            return "/topic/project.0";
        }
        if (isDirectChannel(channelKey)) {
            String enc = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(channelKey.getBytes(StandardCharsets.UTF_8));
            return "/topic/dm." + enc;
        }
        return "/topic/project." + channelKey;
    }

    /**
     * Decode a direct-message STOMP destination back into the canonical channel key.
     * Example: "/topic/dm.{base64url(DM:a|b)}" -> "DM:a|b"
     */
    public static String directChannelKeyFromStompDestination(String stompDestination) {
        if (stompDestination == null) {
            return "";
        }
        String prefix = "/topic/dm.";
        if (!stompDestination.startsWith(prefix)) {
            return "";
        }
        String enc = stompDestination.substring(prefix.length()).trim();
        if (enc.isEmpty()) {
            return "";
        }
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(enc);
            String decoded = new String(bytes, StandardCharsets.UTF_8);
            return decoded;
        } catch (IllegalArgumentException ex) {
            return "";
        }
    }

    private static String normalizeEmail(String e) {
        return e == null ? "" : e.trim().toLowerCase();
    }
}
