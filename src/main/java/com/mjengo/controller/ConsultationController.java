package com.mjengo.controller;

import com.mjengo.model.ConsultationRequest;
import com.mjengo.service.ConsultationService;
import com.mjengo.service.NotificationService;
import com.mjengo.service.ProjectService;
import com.mjengo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clients book consultations with engineers, PMs, admins, or contractors.
 */
@Controller
public class ConsultationController {

    private final ConsultationService consultationService;
    private final ProjectService projectService;
    private final UserService userService;
    private final NotificationService notificationService;

    public ConsultationController(ConsultationService consultationService, ProjectService projectService,
            UserService userService, NotificationService notificationService) {
        this.consultationService = consultationService;
        this.projectService = projectService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/consultations")
    public String clientConsultations(HttpSession session, Model model) {
        if (!"CLIENT".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }
        String email = (String) session.getAttribute("userEmail");
        model.addAttribute("title", "Book a consultation");
        model.addAttribute("activeModule", "consultations");
        model.addAttribute("myRequests", consultationService.getForClient(email));
        model.addAttribute("projects", projectService.findByClientEmail(email));
        return "consultations";
    }

    @PostMapping("/consultations/book")
    public String bookConsultation(@RequestParam("consulteeRole") String consulteeRole,
            @RequestParam(value = "projectId", defaultValue = "0") long projectId,
            @RequestParam("topic") String topic,
            @RequestParam("preferredSlot") String preferredSlot,
            @RequestParam(value = "notes", required = false, defaultValue = "") String notes,
            HttpSession session,
            RedirectAttributes ra) {
        if (!"CLIENT".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }
        String email = (String) session.getAttribute("userEmail");
        String name = (String) session.getAttribute("userName");
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        ConsultationRequest r = new ConsultationRequest();
        r.setClientEmail(email);
        r.setClientName(name != null ? name : email);
        r.setConsulteeRole(consulteeRole != null ? consulteeRole.toUpperCase() : "ENGINEER");
        r.setTopic(topic);
        r.setPreferredSlot(preferredSlot);
        r.setNotes(notes);
        r.setStatus("REQUESTED");
        r.setCreatedAt(ts);

        String projectLabel = "—";
        r.setProjectId(0);
        r.setProjectName("");
        if (projectId > 0) {
            var po = projectService.getById(projectId);
            if (po.isPresent() && email != null && email.equalsIgnoreCase(po.get().getClientEmail())) {
                r.setProjectId(projectId);
                r.setProjectName(po.get().getName() != null ? po.get().getName() : "");
                projectLabel = r.getProjectName().isEmpty() ? "—" : r.getProjectName();
            }
        }

        consultationService.add(r);

        String summary = "Consultation request from " + r.getClientName() + ": " + topic
                + " (slot: " + preferredSlot + ")";
        for (var u : userService.findByRole(r.getConsulteeRole())) {
            notificationService.notify(u.getEmail(), "CONSULTATION", summary, "/consultations/inbox");
        }
        notificationService.broadcast("CONSULTATION",
                summary + " — Project: " + projectLabel,
                "/consultations/inbox");

        ra.addFlashAttribute("success", "Your consultation request was sent. You will be contacted using your account email.");
        return "redirect:/consultations";
    }

    @GetMapping("/consultations/inbox")
    public String inbox(HttpSession session, Model model) {
        String role = (String) session.getAttribute("userRole");
        if (role == null)
            return "redirect:/login";
        if (!List.of("ADMIN", "PROJECT_MANAGER", "ENGINEER", "CONTRACTOR").contains(role)) {
            return "redirect:/";
        }
        model.addAttribute("title", "Consultation inbox");
        model.addAttribute("activeModule", "consultations_inbox");
        model.addAttribute("requests", consultationService.getAll());
        return "consultations_inbox";
    }
}
