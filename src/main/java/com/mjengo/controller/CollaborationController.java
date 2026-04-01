package com.mjengo.controller;

import com.mjengo.model.EntityComment;
import com.mjengo.service.CommentService;
import com.mjengo.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Client approvals and threaded comments on tasks, reports, and designs.
 */
@Controller
public class CollaborationController {

    private final ProjectService projectService;
    private final CommentService commentService;

    public CollaborationController(ProjectService projectService, CommentService commentService) {
        this.projectService = projectService;
        this.commentService = commentService;
    }

    @GetMapping("/collaboration/approvals")
    public String approvalsPage(HttpSession session, Model model) {
        String role = (String) session.getAttribute("userRole");
        if (role == null) {
            return "redirect:/login";
        }
        model.addAttribute("title", "Approvals");
        model.addAttribute("activeModule", "approvals");
        String email = (String) session.getAttribute("userEmail");
        if ("CLIENT".equals(role)) {
            model.addAttribute("approvalProjects", projectService.findByClientEmail(email));
        } else {
            model.addAttribute("approvalProjects", projectService.getAll());
        }
        return "approvals";
    }

    @PostMapping("/collaboration/approve")
    public String approve(@RequestParam("projectId") long projectId,
            @RequestParam("approvalType") String approvalType,
            @RequestParam("decision") String decision,
            HttpSession session,
            RedirectAttributes ra) {
        if (!"CLIENT".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }
        String email = (String) session.getAttribute("userEmail");
        boolean ok = projectService.updateClientApproval(projectId, approvalType, decision, email);
        if (ok) {
            ra.addFlashAttribute("success", "Approval updated.");
        } else {
            ra.addFlashAttribute("error", "You cannot approve this project.");
        }
        return "redirect:/collaboration/approvals";
    }

    @PostMapping("/collaboration/comments/add")
    public String addComment(@RequestParam("targetKey") String targetKey,
            @RequestParam("body") String body,
            @RequestParam("returnPath") String returnPath,
            HttpSession session,
            RedirectAttributes ra) {
        String name = (String) session.getAttribute("userName");
        String role = (String) session.getAttribute("userRole");
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        commentService.add(new EntityComment(targetKey, name, role, body.trim(), ts));
        ra.addFlashAttribute("success", "Comment added.");
        return "redirect:" + returnPath;
    }
}
