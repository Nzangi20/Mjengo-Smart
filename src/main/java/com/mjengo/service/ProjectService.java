package com.mjengo.service;

import com.mjengo.model.Project;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProjectService {
    private final List<Project> projects = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<Project> getAll() {
        return projects;
    }

    public Optional<Project> getById(long id) {
        return projects.stream().filter(p -> p.getId() == id).findFirst();
    }

    public Project add(Project project) {
        project.setId(idCounter.getAndIncrement());
        projects.add(project);
        return project;
    }

    public void delete(long id) {
        projects.removeIf(p -> p.getId() == id);
    }

    public int countByStatus(String status) {
        return (int) projects.stream().filter(p -> p.getStatus().equals(status)).count();
    }

    public double totalBudget() {
        return projects.stream().mapToDouble(Project::getBudget).sum();
    }

    public double totalSpent() {
        return projects.stream().mapToDouble(Project::getSpent).sum();
    }

    public void linkSiteAnalysis(long projectId, long analysisId) {
        getById(projectId).ifPresent(p -> p.setSiteAnalysisResultId(analysisId));
    }

    public void linkCostEstimate(long projectId, long estimateId) {
        getById(projectId).ifPresent(p -> p.setCostEstimateId(estimateId));
    }

    public List<Project> findByClientEmail(String email) {
        if (email == null || email.isBlank()) {
            return List.of();
        }
        return projects.stream()
                .filter(p -> email.equalsIgnoreCase(p.getClientEmail()))
                .toList();
    }

    /**
     * True if this project exists and is owned by the given client email (multi-tenant boundary).
     */
    public boolean clientOwnsProject(long projectId, String clientEmail) {
        if (clientEmail == null || clientEmail.isBlank()) {
            return false;
        }
        return getById(projectId)
                .map(p -> clientEmail.equalsIgnoreCase(p.getClientEmail()))
                .orElse(false);
    }

    /**
     * Client-only: update design / budget / timeline approval flags.
     */
    public boolean updateClientApproval(long projectId, String type, String decision, String clientEmail) {
        Optional<Project> opt = getById(projectId);
        if (opt.isEmpty() || clientEmail == null
                || !clientEmail.equalsIgnoreCase(opt.get().getClientEmail())) {
            return false;
        }
        String v = "APPROVE".equalsIgnoreCase(decision) ? "APPROVED" : "REJECTED";
        Project p = opt.get();
        switch (type != null ? type.toUpperCase() : "") {
            case "DESIGN" -> p.setDesignApproval(v);
            case "BUDGET" -> p.setBudgetApproval(v);
            case "TIMELINE" -> p.setTimelineApproval(v);
            default -> {
                return false;
            }
        }
        return true;
    }
}
