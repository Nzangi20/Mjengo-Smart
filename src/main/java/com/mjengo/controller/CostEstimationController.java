package com.mjengo.controller;

import com.mjengo.model.CostEstimate;
import com.mjengo.service.CostEstimationService;
import com.mjengo.service.NotificationService;
import com.mjengo.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * BoQ cost engine — engineers and PMs generate; clients review totals and breakdown.
 */
@Controller
public class CostEstimationController {

    private final CostEstimationService costEstimationService;
    private final ProjectService projectService;
    private final NotificationService notificationService;

    public CostEstimationController(CostEstimationService costEstimationService, ProjectService projectService,
            NotificationService notificationService) {
        this.costEstimationService = costEstimationService;
        this.projectService = projectService;
        this.notificationService = notificationService;
    }

    @GetMapping("/cost-estimation")
    public String estimationPage(HttpSession session, Model model,
            @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("title", "Cost Estimation (BoQ)");
        model.addAttribute("activeModule", "cost_estimation");
        model.addAttribute("generated", Boolean.FALSE);
        String role = (String) session.getAttribute("userRole");
        String email = (String) session.getAttribute("userEmail");

        if ("forbidden".equals(error)) {
            model.addAttribute("pageError", "You do not have permission to view that estimate.");
        } else if ("missing".equals(error)) {
            model.addAttribute("pageError", "That estimate could not be found.");
        }

        if ("CLIENT".equals(role)) {
            model.addAttribute("readOnlyClient", Boolean.TRUE);
            model.addAttribute("estimates", costEstimationService.getVisibleToClient(email));
            model.addAttribute("projects", projectService.findByClientEmail(email));
        } else {
            model.addAttribute("readOnlyClient", Boolean.FALSE);
            model.addAttribute("estimates", costEstimationService.getAll());
            model.addAttribute("projects", projectService.getAll());
        }
        return "estimation";
    }

    /**
     * Client (or staff) read-only BoQ detail view.
     */
    @GetMapping("/cost-estimation/view")
    public String viewEstimate(@RequestParam("id") long id, HttpSession session, Model model) {
        String role = (String) session.getAttribute("userRole");
        String email = (String) session.getAttribute("userEmail");

        var opt = costEstimationService.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/cost-estimation?error=missing";
        }
        CostEstimate estimate = opt.get();

        if ("CLIENT".equals(role)) {
            if (!costEstimationService.isVisibleToClient(estimate, email)) {
                return "redirect:/cost-estimation?error=forbidden";
            }
            model.addAttribute("readOnlyClient", Boolean.TRUE);
            model.addAttribute("estimates", costEstimationService.getVisibleToClient(email));
            model.addAttribute("projects", projectService.findByClientEmail(email));
        } else {
            model.addAttribute("readOnlyClient", Boolean.FALSE);
            model.addAttribute("estimates", costEstimationService.getAll());
            model.addAttribute("projects", projectService.getAll());
        }

        model.addAttribute("title", "BoQ Detail");
        model.addAttribute("activeModule", "cost_estimation");
        model.addAttribute("estimate", estimate);
        model.addAttribute("generated", Boolean.TRUE);
        return "estimation";
    }

    @PostMapping("/cost-estimation/generate")
    public String generateEstimate(@RequestParam("projectName") String projectName,
            @RequestParam("projectType") String projectType,
            @RequestParam("location") String location,
            @RequestParam("areaSqm") double areaSqm,
            @RequestParam("floors") int floors,
            @RequestParam(value = "linkedProjectId", required = false) Long linkedProjectId,
            @RequestParam(value = "designStyle", defaultValue = "STANDARD") String designStyle,
            @RequestParam(value = "ownerClientEmail", required = false, defaultValue = "") String ownerClientEmail,
            HttpSession session,
            Model model) {

        if ("CLIENT".equals(session.getAttribute("userRole"))) {
            return "redirect:/cost-estimation?error=forbidden";
        }

        String owner = ownerClientEmail != null ? ownerClientEmail.trim() : "";
        CostEstimate estimate = costEstimationService.generate(projectName, projectType, location, areaSqm, floors,
                linkedProjectId, designStyle, owner.isEmpty() ? null : owner);
        if (linkedProjectId != null && linkedProjectId > 0) {
            projectService.getById(linkedProjectId).ifPresent(p -> {
                if (p.getClientEmail() != null && !p.getClientEmail().isBlank()) {
                    notificationService.notify(p.getClientEmail(), "APPROVAL_REQUEST",
                            "BoQ estimate ready for review: " + projectName,
                            "/cost-estimation");
                }
            });
        }
        model.addAttribute("title", "Cost Estimation Result");
        model.addAttribute("activeModule", "cost_estimation");
        model.addAttribute("estimate", estimate);
        model.addAttribute("estimates", costEstimationService.getAll());
        model.addAttribute("projects", projectService.getAll());
        model.addAttribute("generated", Boolean.TRUE);
        model.addAttribute("readOnlyClient", Boolean.FALSE);
        return "estimation";
    }
}
