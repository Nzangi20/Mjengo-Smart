package com.mjengo.service;

import com.mjengo.model.Project;
import com.mjengo.model.SiteAnalysisResult;
import com.mjengo.model.SiteSurveyData;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Smart site suitability analysis engine.
 * Scores construction sites on soil, slope, flood risk, accessibility, water
 * table, and seismic factors.
 * Provides foundation recommendations and cost impact estimates.
 */
@Service
public class SiteAnalysisService {

    private static final AtomicLong ID_GEN = new AtomicLong(1);
    private final List<SiteAnalysisResult> results = new ArrayList<>();
    private final ProjectService projectService;
    private final PersistentStoreService store;

    public SiteAnalysisService(ProjectService projectService, PersistentStoreService store) {
        this.projectService = projectService;
        this.store = store;
        results.addAll(store.loadList("site_analyses", SiteAnalysisResult.class));
        ID_GEN.set(results.stream().map(SiteAnalysisResult::getId).max(Long::compareTo).orElse(0L) + 1L);
    }

    /**
     * Analyze a site based on survey data and return a suitability result.
     */
    public SiteAnalysisResult analyze(SiteSurveyData data) {
        SiteAnalysisResult result = new SiteAnalysisResult();
        result.setId(ID_GEN.getAndIncrement());
        result.setSiteName(data.getSiteName());
        result.setAnalyzedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        // Score each factor (0-20 points each, total max 120 → normalized to 100)
        int soilScore = scoreSoil(data.getSoilType());
        int slopeScore = scoreSlope(data.getSlopeAngle());
        int floodScore = scoreFloodRisk(data.getFloodRisk());
        int accessScore = scoreAccessibility(data.getAccessibility());
        int utilitiesScore = scoreUtilities(data.getUtilitiesRating());
        int waterTableScore = scoreWaterTable(data.getWaterTable());
        int seismicScore = scoreSeismicZone(data.getSeismicZone());

        result.setSoilScore(soilScore);
        result.setSlopeScore(slopeScore);
        result.setFloodScore(floodScore);
        result.setAccessScore(accessScore);
        result.setUtilitiesScore(utilitiesScore);
        result.setWaterTableScore(waterTableScore);
        result.setSeismicScore(seismicScore);

        int rawScore = soilScore + slopeScore + floodScore + accessScore + utilitiesScore + waterTableScore
                + seismicScore;
        double normalizedScore = Math.round((rawScore / 140.0) * 100.0);
        result.setSuitabilityScore(normalizedScore);

        if (data.getProjectId() != null && !data.getProjectId().isBlank()) {
            try {
                result.setProjectId(Long.parseLong(data.getProjectId().trim()));
            } catch (NumberFormatException ignored) {
                result.setProjectId(0);
            }
        }
        result.setLatitude(data.getLatitude());
        result.setLongitude(data.getLongitude());

        // Determine status
        if (normalizedScore >= 70) {
            result.setStatus("SUITABLE");
            result.setStatusColor("emerald");
        } else if (normalizedScore >= 45) {
            result.setStatus("MODERATE");
            result.setStatusColor("amber");
        } else {
            result.setStatus("NOT_SUITABLE");
            result.setStatusColor("red");
        }

        // Identify risks
        List<String> risks = new ArrayList<>();
        if (soilScore < 10)
            risks.add("Poor soil bearing capacity — requires deep foundation treatment");
        if (slopeScore < 10)
            risks.add("Steep terrain — significant earthworks and retaining walls needed");
        if (floodScore < 10)
            risks.add("High flood exposure — drainage infrastructure critical");
        if (accessScore < 10)
            risks.add("Limited site access — construction logistics will be challenging");
        if (utilitiesScore < 10)
            risks.add("Weak utilities provision — temporary power/water may add cost and delay");
        if (waterTableScore < 10)
            risks.add("Shallow water table — dewatering required during excavation");
        if (seismicScore < 10)
            risks.add("Seismic risk zone — earthquake-resistant design mandatory");
        if (risks.isEmpty())
            risks.add("No significant construction risks identified");
        result.setRisks(risks);

        // Recommendations
        List<String> recs = new ArrayList<>();
        result.setFoundationType(recommendFoundation(data.getSoilType(), data.getSlopeAngle()));
        recs.add("Recommended foundation: " + result.getFoundationType());

        if ("PEAT".equals(data.getSoilType()) || "SILT".equals(data.getSoilType())) {
            recs.add("Conduct detailed geotechnical investigation before design finalization");
        }
        if (data.getSlopeAngle() > 15) {
            recs.add("Engage structural engineer for slope stability analysis");
        }
        if ("HIGH".equals(data.getFloodRisk())) {
            recs.add("Design elevated foundation with comprehensive storm-water drainage system");
        }
        if ("SHALLOW".equals(data.getWaterTable())) {
            recs.add("Plan for dewatering during foundation works; consider waterproof membrane");
        }
        if ("HIGH".equals(data.getSeismicZone())) {
            recs.add("Design to seismic code EN 1998; use reinforced concrete frame construction");
        }
        if ("POOR".equals(data.getAccessibility())) {
            recs.add("Plan temporary access road; consider smaller equipment for tight access");
        }
        if (normalizedScore >= 70) {
            recs.add("Site is well-suited for standard construction procedures");
        }
        result.setRecommendations(recs);

        // Cost impact
        double costMul = 1.0;
        if (soilScore < 10)
            costMul += 0.12;
        if (slopeScore < 10)
            costMul += 0.10;
        if (floodScore < 10)
            costMul += 0.08;
        if (waterTableScore < 10)
            costMul += 0.07;
        if (seismicScore < 10)
            costMul += 0.10;
        if (accessScore < 10)
            costMul += 0.05;
        if (utilitiesScore < 10)
            costMul += 0.04;
        result.setCostMultiplier(Math.round(costMul * 100.0) / 100.0);

        if (costMul <= 1.05) {
            result.setCostImpact("LOW");
        } else if (costMul <= 1.20) {
            result.setCostImpact("MODERATE");
        } else {
            result.setCostImpact("HIGH");
        }

        results.add(result);
        save();
        return result;
    }

    private int scoreSoil(String soilType) {
        switch (soilType) {
            case "ROCK":
                return 20;
            case "SANDY":
                return 17;
            case "LOAM":
                return 15;
            case "CLAY":
                return 12;
            case "SILT":
                return 8;
            case "PEAT":
                return 4;
            default:
                return 10;
        }
    }

    private int scoreSlope(double angle) {
        if (angle <= 5)
            return 20;
        if (angle <= 10)
            return 16;
        if (angle <= 15)
            return 12;
        if (angle <= 25)
            return 8;
        if (angle <= 35)
            return 4;
        return 2;
    }

    private int scoreFloodRisk(String risk) {
        switch (risk) {
            case "LOW":
                return 20;
            case "MODERATE":
                return 12;
            case "HIGH":
                return 5;
            default:
                return 10;
        }
    }

    private int scoreAccessibility(String access) {
        switch (access) {
            case "EXCELLENT":
                return 20;
            case "GOOD":
                return 16;
            case "FAIR":
                return 10;
            case "POOR":
                return 4;
            default:
                return 10;
        }
    }

    private int scoreWaterTable(String level) {
        switch (level) {
            case "DEEP":
                return 20;
            case "MODERATE":
                return 14;
            case "SHALLOW":
                return 6;
            default:
                return 10;
        }
    }

    private int scoreSeismicZone(String zone) {
        switch (zone) {
            case "LOW":
                return 20;
            case "MODERATE":
                return 12;
            case "HIGH":
                return 5;
            default:
                return 10;
        }
    }

    private String recommendFoundation(String soilType, double slopeAngle) {
        if ("ROCK".equals(soilType))
            return "Pad / Strip Foundation on rock";
        if ("PEAT".equals(soilType) || "SILT".equals(soilType))
            return "Pile Foundation (driven or bored)";
        if (slopeAngle > 20)
            return "Stepped Strip Foundation with retaining walls";
        if ("CLAY".equals(soilType))
            return "Raft Foundation (floating slab)";
        if ("SANDY".equals(soilType))
            return "Strip Foundation with compacted base";
        return "Standard Strip Foundation";
    }

    public List<SiteAnalysisResult> getAll() {
        return results;
    }

    public Optional<SiteAnalysisResult> findById(long id) {
        return results.stream().filter(r -> r.getId() == id).findFirst();
    }

    /**
     * All site analyses tied to this client's projects: every engineer run with that project selected,
     * plus analyses referenced from {@link Project#getSiteAnalysisResultId()} (latest link per project).
     */
    public List<SiteAnalysisResult> listVisibleToClient(String clientEmail) {
        if (clientEmail == null || clientEmail.isBlank()) {
            return List.of();
        }
        List<Project> mine = projectService.findByClientEmail(clientEmail);
        if (mine.isEmpty()) {
            return List.of();
        }
        Set<Long> linkedIds = mine.stream()
                .map(Project::getSiteAnalysisResultId)
                .filter(id -> id > 0)
                .collect(Collectors.toSet());
        return results.stream()
                .filter(r -> isVisibleToClient(r, clientEmail, linkedIds))
                .sorted(Comparator.comparingLong(SiteAnalysisResult::getId).reversed())
                .toList();
    }

    public boolean isVisibleToClient(SiteAnalysisResult r, String clientEmail) {
        if (r == null || clientEmail == null || clientEmail.isBlank()) {
            return false;
        }
        List<Project> mine = projectService.findByClientEmail(clientEmail);
        if (mine.isEmpty()) {
            return false;
        }
        Set<Long> linkedIds = mine.stream()
                .map(Project::getSiteAnalysisResultId)
                .filter(id -> id > 0)
                .collect(Collectors.toSet());
        return isVisibleToClient(r, clientEmail, linkedIds);
    }

    private boolean isVisibleToClient(SiteAnalysisResult r, String clientEmail,
            Set<Long> linkedAnalysisIdsFromProjects) {
        if (r.getProjectId() > 0) {
            return projectService.clientOwnsProject(r.getProjectId(), clientEmail);
        }
        return linkedAnalysisIdsFromProjects.contains(r.getId());
    }

    private int scoreUtilities(String rating) {
        switch (rating != null ? rating : "GOOD") {
            case "EXCELLENT":
                return 20;
            case "GOOD":
                return 16;
            case "FAIR":
                return 10;
            case "POOR":
                return 4;
            default:
                return 10;
        }
    }

    private void save() {
        store.saveList("site_analyses", results);
    }
}
