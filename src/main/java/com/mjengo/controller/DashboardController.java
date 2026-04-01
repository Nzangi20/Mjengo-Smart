package com.mjengo.controller;

import com.mjengo.model.EntityComment;
import com.mjengo.model.Task;
import com.mjengo.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central controller routing dashboards, module pages, and admin functions.
 * Injects live service data into each dashboard and module template.
 */
@Controller
public class DashboardController {

    private final ProjectService projectService;
    private final WorkerService workerService;
    private final MaterialService materialService;
    private final TaskService taskService;
    private final IncidentService incidentService;
    private final TransactionService transactionService;
    private final UserService userService;
    private final CommentService commentService;

    public DashboardController(ProjectService projectService, WorkerService workerService,
            MaterialService materialService, TaskService taskService,
            IncidentService incidentService, TransactionService transactionService,
            UserService userService, CommentService commentService) {
        this.projectService = projectService;
        this.workerService = workerService;
        this.materialService = materialService;
        this.taskService = taskService;
        this.incidentService = incidentService;
        this.transactionService = transactionService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping("/")
    public String dashboard(HttpSession session, Model model) {
        String role = (String) session.getAttribute("userRole");
        if (role == null)
            return "redirect:/login";

        model.addAttribute("activeModule", "dashboard");

        if ("CLIENT".equals(role)) {
            String email = (String) session.getAttribute("userEmail");
            List<com.mjengo.model.Project> clientProjects = projectService.findByClientEmail(email);
            model.addAttribute("projects", clientProjects);
            model.addAttribute("totalProjects", clientProjects.size());
            double tb = clientProjects.stream().mapToDouble(com.mjengo.model.Project::getBudget).sum();
            double ts = clientProjects.stream().mapToDouble(com.mjengo.model.Project::getSpent).sum();
            model.addAttribute("totalBudget", tb);
            model.addAttribute("totalSpent", ts);
        } else {
            model.addAttribute("projects", projectService.getAll());
            model.addAttribute("totalProjects", projectService.getAll().size());
            model.addAttribute("totalBudget", projectService.totalBudget());
            model.addAttribute("totalSpent", projectService.totalSpent());
        }
        model.addAttribute("activeSites",
                projectService.countByStatus("ON_TRACK") + projectService.countByStatus("AT_RISK"));

        switch (role) {
            case "ADMIN":
                model.addAttribute("title", "Admin Dashboard");
                model.addAttribute("totalUsers", userService.getAllUsers().size());
                model.addAttribute("transactions", transactionService.getAll());
                model.addAttribute("incidents", incidentService.getAll());
                return "dashboard_admin";
            case "PROJECT_MANAGER":
                model.addAttribute("title", "Project Manager Dashboard");
                model.addAttribute("pendingTasks",
                        taskService.countByStatus("UPCOMING") + taskService.countByStatus("TODAY"));
                return "dashboard_pm";
            case "ENGINEER":
                model.addAttribute("title", "Engineer Dashboard");
                String eName = (String) session.getAttribute("userName");
                model.addAttribute("myTasks", taskService.getByAssignee(eName));
                model.addAttribute("overdueTasks", taskService.countByStatus("OVERDUE"));
                return "dashboard_engineer";
            case "CONTRACTOR":
                model.addAttribute("title", "Contractor Dashboard");
                model.addAttribute("workers", workerService.getAll());
                model.addAttribute("presentCount", workerService.countByStatus("PRESENT"));
                model.addAttribute("absentCount", workerService.countByStatus("ABSENT"));
                model.addAttribute("materials", materialService.getAll());
                model.addAttribute("lowStockCount",
                        materialService.countByStatus("LOW") + materialService.countByStatus("CRITICAL"));
                return "dashboard_contractor";
            case "CLIENT":
                model.addAttribute("title", "Client Dashboard");
                model.addAttribute("approvalProjects", projectService.findByClientEmail(
                        (String) session.getAttribute("userEmail")));
                return "dashboard_client";
            default:
                model.addAttribute("title", "Dashboard");
                return "dashboard_admin";
        }
    }

    // ---- ADMIN: User Management ----

    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        model.addAttribute("title", "User Management");
        model.addAttribute("activeModule", "admin_users");
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("adminCount", userService.countByRole("ADMIN"));
        model.addAttribute("pmCount", userService.countByRole("PROJECT_MANAGER"));
        model.addAttribute("engineerCount", userService.countByRole("ENGINEER"));
        model.addAttribute("contractorCount", userService.countByRole("CONTRACTOR"));
        model.addAttribute("clientCount", userService.countByRole("CLIENT"));
        return "admin_users";
    }

    @PostMapping("/admin/users/update-role")
    public String updateUserRole(@RequestParam("userId") long userId,
            @RequestParam("newRole") String newRole,
            RedirectAttributes ra) {
        boolean updated = userService.updateRole(userId, newRole);
        if (updated) {
            ra.addFlashAttribute("success", "User role updated to " + newRole + ".");
        } else {
            ra.addFlashAttribute("error", "User not found.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/delete")
    public String deleteUser(@RequestParam("userId") long userId, RedirectAttributes ra) {
        boolean deleted = userService.deleteUser(userId);
        if (deleted) {
            ra.addFlashAttribute("success", "User deleted successfully.");
        } else {
            ra.addFlashAttribute("error", "User not found.");
        }
        return "redirect:/admin/users";
    }

    // ---- MODULE ROUTES: Each injects its service data ----

    @GetMapping("/survey")
    public String survey(Model model) {
        model.addAttribute("title", "Site Survey");
        model.addAttribute("activeModule", "survey");
        model.addAttribute("projects", projectService.getAll());
        return "survey";
    }

    @GetMapping("/planning")
    public String planning(Model model) {
        model.addAttribute("title", "Design & Planning");
        model.addAttribute("activeModule", "planning");
        model.addAttribute("designComments", commentService.getByTargetKey("DESIGN:GLOBAL"));
        return "planning";
    }

    @GetMapping("/estimation")
    public String estimationLegacy() {
        return "redirect:/cost-estimation";
    }

    @GetMapping("/scheduling")
    public String scheduling(Model model) {
        model.addAttribute("title", "Project Scheduling");
        model.addAttribute("activeModule", "scheduling");
        model.addAttribute("tasks", taskService.getAll());
        model.addAttribute("projects", projectService.getAll());
        Map<Long, List<EntityComment>> taskComments = new HashMap<>();
        for (Task t : taskService.getAll()) {
            taskComments.put(t.getId(), commentService.getByTargetKey("TASK:" + t.getId()));
        }
        model.addAttribute("taskComments", taskComments);
        return "scheduling";
    }

    @GetMapping("/workforce")
    public String workforce(Model model) {
        model.addAttribute("title", "Workforce Management");
        model.addAttribute("activeModule", "workforce");
        model.addAttribute("workers", workerService.getAll());
        model.addAttribute("projects", projectService.getAll());
        model.addAttribute("presentCount", workerService.countByStatus("PRESENT"));
        model.addAttribute("absentCount", workerService.countByStatus("ABSENT"));
        return "workforce";
    }

    @GetMapping("/inventory")
    public String inventory(Model model) {
        model.addAttribute("title", "Materials & Inventory");
        model.addAttribute("activeModule", "inventory");
        model.addAttribute("materials", materialService.getAll());
        return "inventory";
    }

    @GetMapping("/monitoring")
    public String monitoring(Model model) {
        model.addAttribute("title", "Construction Monitoring");
        model.addAttribute("activeModule", "monitoring");
        model.addAttribute("projects", projectService.getAll());
        return "monitoring";
    }

    @GetMapping("/risk")
    public String risk(Model model) {
        model.addAttribute("title", "Risk & Safety");
        model.addAttribute("activeModule", "risk");
        model.addAttribute("incidents", incidentService.getAll());
        model.addAttribute("projects", projectService.getAll());
        return "risk";
    }

    @GetMapping("/compliance")
    public String compliance(HttpSession session, Model model) {
        model.addAttribute("title", "Compliance & Permits");
        model.addAttribute("activeModule", "compliance");
        String role = (String) session.getAttribute("userRole");
        if ("CLIENT".equals(role)) {
            model.addAttribute("projects", projectService.findByClientEmail((String) session.getAttribute("userEmail")));
        } else {
            model.addAttribute("projects", projectService.getAll());
        }
        return "compliance";
    }

    @GetMapping("/financials")
    public String financials(Model model) {
        model.addAttribute("title", "Financial Management");
        model.addAttribute("activeModule", "financials");
        model.addAttribute("transactions", transactionService.getAll());
        model.addAttribute("totalAmount", transactionService.totalAmount());
        model.addAttribute("clearedAmount", transactionService.totalByStatus("CLEARED"));
        model.addAttribute("pendingAmount", transactionService.totalByStatus("PENDING"));
        model.addAttribute("projects", projectService.getAll());
        return "financials";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("title", "Reports & Analytics");
        model.addAttribute("activeModule", "reports");
        model.addAttribute("projects", projectService.getAll());
        model.addAttribute("totalBudget", projectService.totalBudget());
        model.addAttribute("totalSpent", projectService.totalSpent());
        model.addAttribute("workers", workerService.getAll());
        model.addAttribute("incidents", incidentService.getAll());
        model.addAttribute("reportComments", commentService.getByTargetKey("REPORT:GLOBAL"));
        return "reports";
    }

    @GetMapping("/maintenance")
    public String maintenance(Model model) {
        model.addAttribute("title", "Maintenance & Assets");
        model.addAttribute("activeModule", "maintenance");
        model.addAttribute("projects", projectService.getAll());
        return "maintenance";
    }
}
