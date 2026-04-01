package com.mjengo.controller;

import com.mjengo.model.*;
import com.mjengo.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles all form submissions (POST requests) across every module.
 * Each POST creates a new entity and triggers notifications.
 */
@Controller
public class FormController {

    private final ProjectService projectService;
    private final WorkerService workerService;
    private final MaterialService materialService;
    private final TaskService taskService;
    private final IncidentService incidentService;
    private final TransactionService transactionService;
    private final NotificationService notificationService;

    public FormController(ProjectService projectService, WorkerService workerService,
            MaterialService materialService, TaskService taskService,
            IncidentService incidentService, TransactionService transactionService,
            NotificationService notificationService) {
        this.projectService = projectService;
        this.workerService = workerService;
        this.materialService = materialService;
        this.taskService = taskService;
        this.incidentService = incidentService;
        this.transactionService = transactionService;
        this.notificationService = notificationService;
    }

    // ---- PROJECT ----
    @PostMapping("/projects/add")
    public String addProject(@RequestParam("name") String name, @RequestParam("location") String location,
            @RequestParam("budget") double budget, @RequestParam("manager") String manager,
            @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
            @RequestParam("description") String description,
            @RequestParam(value = "constructionType", defaultValue = "RESIDENTIAL") String constructionType,
            @RequestParam(value = "requirements", required = false, defaultValue = "") String requirements,
            @RequestParam(value = "clientEmail", required = false, defaultValue = "") String clientEmailField,
            HttpSession session, RedirectAttributes ra) {
        Project p = new Project(0, name, location, "ON_TRACK", budget, 0, 0, manager, startDate, endDate, description);
        p.setConstructionType(constructionType);
        p.setRequirements(requirements != null ? requirements : "");
        String role = (String) session.getAttribute("userRole");
        if ("CLIENT".equals(role)) {
            p.setClientEmail((String) session.getAttribute("userEmail"));
        } else if (clientEmailField != null && !clientEmailField.isBlank()) {
            p.setClientEmail(clientEmailField.trim());
        }
        projectService.add(p);
        // Notify all users about new project
        notificationService.broadcast("INFO",
                "New project created: " + name + " at " + location,
                "/");
        ra.addFlashAttribute("success", "Project '" + name + "' created successfully.");
        return "redirect:/";
    }

    @PostMapping("/projects/delete")
    public String deleteProject(@RequestParam("id") long id, RedirectAttributes ra) {
        projectService.delete(id);
        ra.addFlashAttribute("success", "Project deleted.");
        return "redirect:/";
    }

    // ---- WORKER ----
    @PostMapping("/workers/add")
    public String addWorker(@RequestParam("name") String name, @RequestParam("trade") String trade,
            @RequestParam("phone") String phone, @RequestParam("assignedProject") String project,
            @RequestParam("dailyRate") double rate, RedirectAttributes ra) {
        Worker w = new Worker(0, name, trade, phone, "PRESENT", "07:00", project, rate);
        workerService.add(w);
        notificationService.broadcast("INFO",
                "New worker added: " + name + " (" + trade + ") to " + project,
                "/workforce");
        ra.addFlashAttribute("success", "Worker '" + name + "' added to the roster.");
        return "redirect:/workforce";
    }

    @PostMapping("/workers/delete")
    public String deleteWorker(@RequestParam("id") long id, RedirectAttributes ra) {
        workerService.delete(id);
        ra.addFlashAttribute("success", "Worker removed from register.");
        return "redirect:/workforce";
    }

    // ---- MATERIAL ----
    @PostMapping("/materials/add")
    public String addMaterial(@RequestParam("name") String name, @RequestParam("unit") String unit,
            @RequestParam("currentStock") int stock, @RequestParam("maxStock") int max,
            @RequestParam("unitPrice") double price, @RequestParam("supplier") String supplier,
            RedirectAttributes ra) {
        Material m = new Material(0, name, unit, stock, max, price, supplier);
        materialService.add(m);
        if (stock < max * 0.2) {
            notificationService.broadcast("ALERT",
                    "Low stock alert: " + name + " at " + (stock * 100 / max) + "% capacity",
                    "/inventory");
        }
        ra.addFlashAttribute("success", "Material '" + name + "' added to inventory.");
        return "redirect:/inventory";
    }

    @PostMapping("/materials/delete")
    public String deleteMaterial(@RequestParam("id") long id, RedirectAttributes ra) {
        materialService.delete(id);
        ra.addFlashAttribute("success", "Material removed.");
        return "redirect:/inventory";
    }

    // ---- TASK ----
    @PostMapping("/tasks/add")
    public String addTask(@RequestParam("title") String title, @RequestParam("description") String desc,
            @RequestParam("assignedTo") String assignee, @RequestParam("project") String project,
            @RequestParam("category") String category, @RequestParam("priority") String priority,
            @RequestParam("dueDate") String dueDate, RedirectAttributes ra) {
        Task t = new Task(0, title, desc, assignee, project, category, priority, "UPCOMING", dueDate);
        taskService.add(t);
        notificationService.broadcast("TASK",
                "New task assigned: '" + title + "' to " + assignee,
                "/scheduling");
        ra.addFlashAttribute("success", "Task '" + title + "' assigned to " + assignee + ".");
        return "redirect:/scheduling";
    }

    @PostMapping("/tasks/done")
    public String markTaskDone(@RequestParam("id") long id, RedirectAttributes ra) {
        taskService.markDone(id);
        notificationService.broadcast("INFO", "Task completed!", "/scheduling");
        ra.addFlashAttribute("success", "Task marked as completed.");
        return "redirect:/scheduling";
    }

    @PostMapping("/tasks/delete")
    public String deleteTask(@RequestParam("id") long id, RedirectAttributes ra) {
        taskService.delete(id);
        ra.addFlashAttribute("success", "Task deleted.");
        return "redirect:/scheduling";
    }

    // ---- INCIDENT ----
    @PostMapping("/incidents/add")
    public String addIncident(@RequestParam("title") String title, @RequestParam("severity") String severity,
            @RequestParam("type") String type, @RequestParam("description") String desc,
            @RequestParam("project") String project, @RequestParam("reportedBy") String reporter,
            @RequestParam("date") String date, @RequestParam("actionTaken") String action,
            RedirectAttributes ra) {
        Incident i = new Incident(0, title, severity, type, desc, project, reporter, date, action);
        incidentService.add(i);
        notificationService.broadcast("ALERT",
                "Safety Incident [" + severity + "]: " + title + " at " + project,
                "/risk");
        ra.addFlashAttribute("success", "Safety incident reported successfully.");
        return "redirect:/risk";
    }

    // ---- TRANSACTION ----
    @PostMapping("/transactions/add")
    public String addTransaction(@RequestParam("payee") String payee, @RequestParam("category") String category,
            @RequestParam("amount") double amount, @RequestParam("project") String project,
            @RequestParam("date") String date, RedirectAttributes ra) {
        String txnId = "TXN-" + (4400 + transactionService.getAll().size() + 1);
        Transaction t = new Transaction(0, txnId, date, payee, category, amount, "PENDING", project);
        transactionService.add(t);
        notificationService.broadcast("INFO",
                "New transaction: " + txnId + " — KES " + String.format("%,.0f", amount) + " to " + payee,
                "/financials");
        ra.addFlashAttribute("success",
                "Transaction " + txnId + " recorded (KES " + String.format("%,.0f", amount) + ").");
        return "redirect:/financials";
    }
}
