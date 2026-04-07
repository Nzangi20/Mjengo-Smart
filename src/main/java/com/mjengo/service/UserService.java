package com.mjengo.service;

import com.mjengo.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;

/**
 * In-memory user store with construction-industry roles.
 * Supports 5 roles:
 * - ADMIN : System administrator — full access, user management
 * - PROJECT_MANAGER : Oversees projects — cost estimation, scheduling,
 * coordination
 * - ENGINEER : Technical role — surveys, designs, site analysis
 * - CONTRACTOR : Operations — workforce, materials, daily site ops
 * - CLIENT : Property Owner / Investor — milestone tracking, reports
 */
@Service
public class UserService {

    private final List<User> users = new ArrayList<>();
    private final PersistentStoreService store;

    public UserService(PersistentStoreService store) {
        this.store = store;
        users.addAll(store.loadList("users", User.class));
        long nextId = users.stream().map(User::getId).max(Comparator.naturalOrder()).orElse(0L) + 1L;
        User.syncIdGenerator(nextId);
        ensureDefaultAdmin();
        save();
    }

    /**
     * Register a new user. Returns false if email already exists.
     */
    public boolean register(User user) {
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return false;
        }
        Optional<User> existing = users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))
                .findFirst();
        if (existing.isPresent()) {
            return false;
        }
        users.add(user);
        save();
        return true;
    }

    /**
     * Authenticate user by email and password.
     */
    public Optional<User> authenticate(String email, String password) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password))
                .findFirst();
    }

    /**
     * Get all registered users (for admin panel).
     */
    public List<User> getAllUsers() {
        return users;
    }

    /**
     * Find user by ID.
     */
    public Optional<User> findById(long id) {
        return users.stream().filter(u -> u.getId() == id).findFirst();
    }

    /**
     * Find user by email.
     */
    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    /**
     * Update a user's role (Admin function).
     */
    public boolean updateRole(long userId, String newRole) {
        if ("ADMIN".equalsIgnoreCase(newRole)) {
            return false;
        }
        Optional<User> user = findById(userId);
        if (user.isPresent()) {
            user.get().setRole(newRole.toUpperCase());
            save();
            return true;
        }
        return false;
    }

    /**
     * Delete a user by ID (Admin function).
     */
    public boolean deleteUser(long userId) {
        boolean removed = users.removeIf(u -> u.getId() == userId && !isDefaultAdmin(u));
        if (removed) {
            save();
        }
        return removed;
    }

    /**
     * Count users by role.
     */
    public long countByRole(String role) {
        return users.stream().filter(u -> u.getRole().equalsIgnoreCase(role)).count();
    }

    /**
     * Users with a given role (for routing consultation notifications).
     */
    public List<User> findByRole(String role) {
        if (role == null) {
            return List.of();
        }
        return users.stream()
                .filter(u -> u.getRole().equalsIgnoreCase(role))
                .toList();
    }

    private void ensureDefaultAdmin() {
        boolean hasDefault = users.stream().anyMatch(this::isDefaultAdmin);
        if (!hasDefault) {
            users.add(new User("System Administrator", "admin@gmail.com", "123456", "ADMIN"));
        } else {
            users.stream()
                    .filter(this::isDefaultAdmin)
                    .forEach(u -> u.setRole("ADMIN"));
        }
    }

    private boolean isDefaultAdmin(User user) {
        return user != null
                && "admin@gmail.com".equalsIgnoreCase(user.getEmail())
                && "123456".equals(user.getPassword());
    }

    private void save() {
        store.saveList("users", users);
    }
}
