package com.mjengo.service;

import com.mjengo.model.CostEstimate;
import com.mjengo.model.CostLineItem;
import com.mjengo.model.Project;
import com.mjengo.model.SiteAnalysisResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Smart cost estimation engine that generates a Bill of Quantities (BoQ)
 * based on project parameters: type, area, floors, and location.
 * Applies regional and design-type multipliers, optional design finish tier,
 * and site-difficulty factor from linked suitability analysis.
 */
@Service
public class CostEstimationService {

    private static final AtomicLong ID_GEN = new AtomicLong(1);
    private final List<CostEstimate> estimates = new ArrayList<>();
    private final ProjectService projectService;
    private final SiteAnalysisService siteAnalysisService;

    public CostEstimationService(ProjectService projectService, SiteAnalysisService siteAnalysisService) {
        this.projectService = projectService;
        this.siteAnalysisService = siteAnalysisService;
    }

    // Regional cost multipliers (base = Nairobi = 1.0)
    private static final Map<String, Double> REGION_MULTIPLIERS = new LinkedHashMap<>();
    static {
        REGION_MULTIPLIERS.put("Nairobi", 1.0);
        REGION_MULTIPLIERS.put("Mombasa", 1.05);
        REGION_MULTIPLIERS.put("Kisumu", 0.90);
        REGION_MULTIPLIERS.put("Nakuru", 0.88);
        REGION_MULTIPLIERS.put("Eldoret", 0.85);
        REGION_MULTIPLIERS.put("Thika", 0.92);
        REGION_MULTIPLIERS.put("Other", 0.95);
    }

    // Project type multipliers
    private static final Map<String, Double> TYPE_MULTIPLIERS = new LinkedHashMap<>();
    static {
        TYPE_MULTIPLIERS.put("RESIDENTIAL", 1.0);
        TYPE_MULTIPLIERS.put("COMMERCIAL", 1.25);
        TYPE_MULTIPLIERS.put("INDUSTRIAL", 1.40);
        TYPE_MULTIPLIERS.put("INFRASTRUCTURE", 1.55);
    }

    private static final Map<String, Double> DESIGN_FINISH = new LinkedHashMap<>();
    static {
        DESIGN_FINISH.put("STANDARD", 1.0);
        DESIGN_FINISH.put("CONTEMPORARY", 1.08);
        DESIGN_FINISH.put("PREMIUM", 1.18);
    }

    /**
     * Generate a full BoQ estimate from project parameters.
     *
     * @param linkedProjectId optional project id to pull site cost multiplier from latest analysis
     * @param designStyle     STANDARD, CONTEMPORARY, PREMIUM (materials weighting)
     */
    public CostEstimate generate(String projectName, String projectType, String location, double areaSqm, int floors,
            Long linkedProjectId, String designStyle, String ownerClientEmailUnlinked) {
        CostEstimate est = new CostEstimate();
        est.setId(ID_GEN.getAndIncrement());
        est.setProjectName(projectName);
        est.setProjectType(projectType);
        est.setLocation(location);
        est.setAreaSqm(areaSqm);
        est.setFloors(floors);
        est.setGeneratedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        double regionMul = REGION_MULTIPLIERS.getOrDefault(location, 0.95);
        double typeMul = TYPE_MULTIPLIERS.getOrDefault(projectType, 1.0);
        double totalArea = areaSqm * floors;

        List<CostLineItem> items = new ArrayList<>();
        int no = 1;

        // ---- MATERIALS ----
        items.add(new CostLineItem(no++, "MATERIAL", "Cement (50kg bags)", Math.ceil(totalArea * 1.2), "bags",
                850 * regionMul));
        items.add(new CostLineItem(no++, "MATERIAL", "River Sand", Math.ceil(totalArea * 0.05), "tonnes",
                3200 * regionMul));
        items.add(new CostLineItem(no++, "MATERIAL", "Ballast / Aggregate", Math.ceil(totalArea * 0.07), "tonnes",
                3800 * regionMul));
        items.add(new CostLineItem(no++, "MATERIAL", "Reinforcement Steel (Y12/Y16)", Math.ceil(totalArea * 0.04),
                "tonnes", 125000 * regionMul));
        items.add(new CostLineItem(no++, "MATERIAL", "Building Blocks (6-inch)", Math.ceil(totalArea * 12), "pcs",
                55 * regionMul));
        items.add(new CostLineItem(no++, "MATERIAL", "Roofing Sheets (gauge 30)", Math.ceil(areaSqm * 1.1), "pcs",
                950 * regionMul));
        items.add(new CostLineItem(no++, "MATERIAL", "Timber (2x4 rafters)", Math.ceil(areaSqm * 0.15), "pcs",
                450 * regionMul));
        items.add(
                new CostLineItem(no++, "MATERIAL", "Plumbing Pipes & Fittings", 1, "lot", totalArea * 120 * regionMul));
        items.add(new CostLineItem(no++, "MATERIAL", "Electrical Wiring & Fixtures", 1, "lot",
                totalArea * 150 * regionMul));
        items.add(new CostLineItem(no++, "MATERIAL", "Paint & Finishing", Math.ceil(totalArea * 2.5), "litres",
                380 * regionMul));
        items.add(new CostLineItem(no++, "MATERIAL", "Windows & Doors", Math.ceil(totalArea * 0.08), "units",
                15000 * regionMul));

        // ---- LABOR ----
        items.add(new CostLineItem(no++, "LABOR", "Foundation & Excavation Works", totalArea, "sqm", 350 * typeMul));
        items.add(new CostLineItem(no++, "LABOR", "Masonry / Blockwork", totalArea, "sqm", 280 * typeMul));
        items.add(new CostLineItem(no++, "LABOR", "Concrete & Slab Works", totalArea, "sqm", 420 * typeMul));
        items.add(new CostLineItem(no++, "LABOR", "Roofing Installation", areaSqm, "sqm", 300 * typeMul));
        items.add(new CostLineItem(no++, "LABOR", "Plumbing Installation", totalArea, "sqm", 180 * typeMul));
        items.add(new CostLineItem(no++, "LABOR", "Electrical Installation", totalArea, "sqm", 200 * typeMul));
        items.add(new CostLineItem(no++, "LABOR", "Plastering & Finishing", totalArea * 2, "sqm", 150 * typeMul));
        items.add(new CostLineItem(no++, "LABOR", "Painting (Interior & Exterior)", totalArea * 2.5, "sqm",
                100 * typeMul));

        // ---- EQUIPMENT ----
        items.add(new CostLineItem(no++, "EQUIPMENT", "Concrete Mixer Hire", Math.ceil(totalArea / 200), "days",
                5000 * regionMul));
        items.add(new CostLineItem(no++, "EQUIPMENT", "Excavator Hire", Math.ceil(totalArea / 500), "days",
                25000 * regionMul));
        items.add(new CostLineItem(no++, "EQUIPMENT", "Scaffolding Hire", Math.max(1, floors - 1) * 2, "months",
                45000 * regionMul));
        items.add(
                new CostLineItem(no++, "EQUIPMENT", "Generator", Math.ceil(totalArea / 300), "days", 8000 * regionMul));
        items.add(new CostLineItem(no, "EQUIPMENT", "Site Tools & Sundries", 1, "lot", 75000 * regionMul));

        double designMul = DESIGN_FINISH.getOrDefault(
                designStyle != null ? designStyle : "STANDARD", 1.0);
        double siteMul = resolveSiteMultiplier(linkedProjectId);
        est.setSiteDifficultyMultiplier(siteMul);
        est.setLinkedProjectId(linkedProjectId != null && linkedProjectId > 0 ? linkedProjectId : 0);

        for (CostLineItem i : items) {
            double t = i.getTotal();
            if ("MATERIAL".equals(i.getCategory())) {
                t *= designMul;
            }
            i.setTotal(Math.round(t * siteMul * 100.0) / 100.0);
        }

        if (siteMul > 1.001) {
            est.setIntegrationNotes("Line totals scaled by site difficulty factor " + siteMul
                    + " from linked suitability analysis (foundation, access, flood, utilities).");
        } else if (linkedProjectId != null && linkedProjectId > 0) {
            est.setIntegrationNotes("No site analysis linked yet — multiplier 1.0. Run site analysis and link project for risk-adjusted costs.");
        }

        est.setItems(items);

        // Calculate totals
        double matTotal = items.stream().filter(i -> "MATERIAL".equals(i.getCategory()))
                .mapToDouble(CostLineItem::getTotal).sum();
        double labTotal = items.stream().filter(i -> "LABOR".equals(i.getCategory()))
                .mapToDouble(CostLineItem::getTotal).sum();
        double eqTotal = items.stream().filter(i -> "EQUIPMENT".equals(i.getCategory()))
                .mapToDouble(CostLineItem::getTotal).sum();

        est.setMaterialTotal(matTotal);
        est.setLaborTotal(labTotal);
        est.setEquipmentTotal(eqTotal);

        double subtotal = matTotal + labTotal + eqTotal;
        est.setSubtotal(subtotal);
        est.setContingency(subtotal * 0.10);
        est.setVat((subtotal + est.getContingency()) * 0.16);
        est.setGrandTotal(subtotal + est.getContingency() + est.getVat());

        if (linkedProjectId != null && linkedProjectId > 0) {
            projectService.getById(linkedProjectId).ifPresent(p -> {
                if (p.getClientEmail() != null && !p.getClientEmail().isBlank()) {
                    est.setOwnerClientEmail(p.getClientEmail());
                }
            });
        } else if (ownerClientEmailUnlinked != null && !ownerClientEmailUnlinked.isBlank()) {
            est.setOwnerClientEmail(ownerClientEmailUnlinked.trim());
        }

        estimates.add(est);
        if (linkedProjectId != null && linkedProjectId > 0) {
            projectService.linkCostEstimate(linkedProjectId, est.getId());
        }
        return est;
    }

    /**
     * Backward-compatible entry without project linkage or design tier.
     */
    public CostEstimate generate(String projectName, String projectType, String location, double areaSqm, int floors) {
        return generate(projectName, projectType, location, areaSqm, floors, null, "STANDARD", null);
    }

    public CostEstimate generate(String projectName, String projectType, String location, double areaSqm, int floors,
            Long linkedProjectId, String designStyle) {
        return generate(projectName, projectType, location, areaSqm, floors, linkedProjectId, designStyle, null);
    }

    private double resolveSiteMultiplier(Long linkedProjectId) {
        if (linkedProjectId == null || linkedProjectId <= 0) {
            return 1.0;
        }
        return projectService.getById(linkedProjectId)
                .filter(p -> p.getSiteAnalysisResultId() > 0)
                .flatMap(p -> siteAnalysisService.findById(p.getSiteAnalysisResultId()))
                .map(SiteAnalysisResult::getCostMultiplier)
                .orElse(1.0);
    }

    public List<CostEstimate> getAll() {
        return estimates;
    }

    public Optional<CostEstimate> findById(long id) {
        return estimates.stream().filter(e -> e.getId() == id).findFirst();
    }

    /**
     * BoQs visible to a client: only projects and proposals explicitly tied to them (no cross-tenant leakage).
     */
    public boolean isVisibleToClient(CostEstimate e, String clientEmail) {
        if (clientEmail == null || clientEmail.isBlank() || e == null) {
            return false;
        }
        List<Project> mine = projectService.findByClientEmail(clientEmail);
        if (mine.isEmpty()) {
            return false;
        }
        if (mine.stream().anyMatch(p -> p.getCostEstimateId() == e.getId())) {
            return true;
        }
        if (e.getLinkedProjectId() > 0) {
            return projectService.clientOwnsProject(e.getLinkedProjectId(), clientEmail);
        }
        if (e.getOwnerClientEmail() != null && !e.getOwnerClientEmail().isBlank()) {
            return clientEmail.equalsIgnoreCase(e.getOwnerClientEmail());
        }
        return false;
    }

    public List<CostEstimate> getVisibleToClient(String clientEmail) {
        return estimates.stream()
                .filter(e -> isVisibleToClient(e, clientEmail))
                .sorted(Comparator.comparingLong(CostEstimate::getId).reversed())
                .toList();
    }
}
