package com.mjengo.controller;

import com.mjengo.model.SiteAnalysisResult;
import com.mjengo.model.SiteSurveyData;
import com.mjengo.service.NotificationService;
import com.mjengo.service.ProjectService;
import com.mjengo.service.SiteAnalysisService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Site suitability analysis — engineers submit data; clients review outputs.
 */
@Controller
public class SiteAnalysisController {

    private final SiteAnalysisService siteAnalysisService;
    private final ProjectService projectService;
    private final NotificationService notificationService;

    public SiteAnalysisController(SiteAnalysisService siteAnalysisService, ProjectService projectService,
            NotificationService notificationService) {
        this.siteAnalysisService = siteAnalysisService;
        this.projectService = projectService;
        this.notificationService = notificationService;
    }

    @GetMapping("/site-analysis")
    public String analysisPage(HttpSession session, Model model,
            @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("title", "Site Suitability Analysis");
        model.addAttribute("activeModule", "site_analysis");
        model.addAttribute("analyzed", Boolean.FALSE);
        String role = (String) session.getAttribute("userRole");
        String email = (String) session.getAttribute("userEmail");
        model.addAttribute("readOnlyClient", "CLIENT".equals(role));

        if ("forbidden".equals(error)) {
            model.addAttribute("pageError", "You do not have permission to run a new analysis from this account.");
        } else if ("missing".equals(error)) {
            model.addAttribute("pageError", "That site analysis could not be found.");
        } else if ("access".equals(error)) {
            model.addAttribute("pageError", "You do not have access to that analysis.");
        }

        if ("CLIENT".equals(role)) {
            model.addAttribute("previousResults", siteAnalysisService.listVisibleToClient(email));
            model.addAttribute("projects", projectService.findByClientEmail(email));
        } else {
            model.addAttribute("previousResults", siteAnalysisService.getAll());
            model.addAttribute("projects", projectService.getAll());
        }
        return "site_analysis";
    }

    /**
     * Read-only detail for one analysis; clients only if it belongs to their projects.
     */
    @GetMapping("/site-analysis/view")
    public String viewAnalysis(@RequestParam("id") long id, HttpSession session, Model model) {
        String role = (String) session.getAttribute("userRole");
        String email = (String) session.getAttribute("userEmail");
        var opt = siteAnalysisService.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/site-analysis?error=missing";
        }
        SiteAnalysisResult result = opt.get();
        if ("CLIENT".equals(role)) {
            if (!siteAnalysisService.isVisibleToClient(result, email)) {
                return "redirect:/site-analysis?error=access";
            }
            model.addAttribute("previousResults", siteAnalysisService.listVisibleToClient(email));
            model.addAttribute("projects", projectService.findByClientEmail(email));
            model.addAttribute("readOnlyClient", true);
        } else {
            model.addAttribute("previousResults", siteAnalysisService.getAll());
            model.addAttribute("projects", projectService.getAll());
            model.addAttribute("readOnlyClient", false);
        }
        model.addAttribute("title", "Site Analysis Detail");
        model.addAttribute("activeModule", "site_analysis");
        model.addAttribute("analyzed", true);
        model.addAttribute("result", result);
        return "site_analysis";
    }

    @PostMapping("/site-analysis/analyze")
    public String analyzeSite(@RequestParam("siteName") String siteName,
            @RequestParam("soilType") String soilType,
            @RequestParam("slopeAngle") double slopeAngle,
            @RequestParam("floodRisk") String floodRisk,
            @RequestParam("accessibility") String accessibility,
            @RequestParam(value = "utilitiesRating", defaultValue = "GOOD") String utilitiesRating,
            @RequestParam("waterTable") String waterTable,
            @RequestParam("seismicZone") String seismicZone,
            @RequestParam(value = "projectId", defaultValue = "0") long projectId,
            @RequestParam(value = "latitude", defaultValue = "0") double latitude,
            @RequestParam(value = "longitude", defaultValue = "0") double longitude,
            HttpSession session,
            Model model) {

        if ("CLIENT".equals(session.getAttribute("userRole"))) {
            return "redirect:/site-analysis?error=forbidden";
        }

        SiteSurveyData data = new SiteSurveyData();
        data.setSiteName(siteName);
        data.setSoilType(soilType);
        data.setSlopeAngle(slopeAngle);
        data.setFloodRisk(floodRisk);
        data.setAccessibility(accessibility);
        data.setUtilitiesRating(utilitiesRating);
        data.setWaterTable(waterTable);
        data.setSeismicZone(seismicZone);
        data.setLatitude(latitude);
        data.setLongitude(longitude);
        if (projectId > 0) {
            data.setProjectId(String.valueOf(projectId));
        }

        SiteAnalysisResult result = siteAnalysisService.analyze(data);

        if (projectId > 0) {
            projectService.linkSiteAnalysis(projectId, result.getId());
            projectService.getById(projectId).ifPresent(p -> {
                if (p.getClientEmail() != null && !p.getClientEmail().isBlank()) {
                    notificationService.notify(p.getClientEmail(), "SITE_ANALYSIS",
                            "Site suitability report ready for " + p.getName() + " (score "
                                    + Math.round(result.getSuitabilityScore()) + "%)",
                            "/site-analysis");
                }
            });
            notificationService.broadcast("RISK_ALERT",
                    "Site analysis: " + siteName + " — suitability "
                            + Math.round(result.getSuitabilityScore()) + "% (" + result.getStatus() + ")",
                    "/site-analysis");
        }

        model.addAttribute("title", "Site Analysis Result");
        model.addAttribute("activeModule", "site_analysis");
        model.addAttribute("result", result);
        model.addAttribute("analyzed", true);
        model.addAttribute("previousResults", siteAnalysisService.getAll());
        model.addAttribute("projects", projectService.getAll());
        model.addAttribute("readOnlyClient", false);
        return "site_analysis";
    }
}
