package com.mjengo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Session authentication and role-based access. Longest-prefix route rules win.
 * Paths not listed are allowed for any authenticated user (role checks on sensitive
 * POST actions are enforced in controllers where needed).
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final List<RouteRule> ROUTE_RULES = new ArrayList<>();

    static {
        route("/admin/users", "ADMIN");
        route("/admin/users/update-role", "ADMIN");
        route("/admin/users/delete", "ADMIN");
        route("/financials", "ADMIN", "PROJECT_MANAGER");
        route("/compliance", "ADMIN", "CLIENT");
        route("/planning", "ADMIN", "PROJECT_MANAGER", "ENGINEER");
        route("/survey", "ADMIN", "PROJECT_MANAGER", "ENGINEER");
        route("/workforce", "ADMIN", "PROJECT_MANAGER", "CONTRACTOR");
        route("/inventory", "ADMIN", "PROJECT_MANAGER", "CONTRACTOR");
        route("/maintenance", "ADMIN", "PROJECT_MANAGER", "CONTRACTOR");
        route("/collaboration/approvals", "ADMIN", "PROJECT_MANAGER", "CLIENT");
        route("/consultations/inbox", "ADMIN", "PROJECT_MANAGER", "ENGINEER", "CONTRACTOR");
        route("/consultations", "CLIENT");
        ROUTE_RULES.sort(Comparator.comparingInt((RouteRule r) -> r.prefix.length()).reversed());
    }

    private static void route(String prefix, String... roles) {
        ROUTE_RULES.add(new RouteRule(prefix, Arrays.asList(roles)));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String uri = request.getRequestURI();

        if (uri.equals("/login") || uri.equals("/register")
                || uri.startsWith("/css") || uri.startsWith("/js") || uri.startsWith("/images")
                || uri.startsWith("/webjars") || uri.startsWith("/error")) {
            return true;
        }

        Object user = request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("/login");
            return false;
        }

        String userRole = (String) request.getSession().getAttribute("userRole");

        // SockJS / STOMP handshake: require session (same as login)
        if (uri.startsWith("/ws")) {
            return true;
        }

        for (RouteRule rule : ROUTE_RULES) {
            if (uri.startsWith(rule.prefix)) {
                if (!rule.roles.contains(userRole)) {
                    response.sendRedirect("/");
                    return false;
                }
                return true;
            }
        }

        return true;
    }

    private record RouteRule(String prefix, List<String> roles) {
    }
}
