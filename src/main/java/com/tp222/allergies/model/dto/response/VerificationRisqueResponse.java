package com.tp222.allergies.model.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

/**
 * DTO pour la réponse de vérification de risque d'allergie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationRisqueResponse {
    
    private boolean risqueDetecte;
    private String niveauRisque; // FAIBLE, MODERE, ELEVE, CRITIQUE
    private int scoreRisque; // 0-100
    
    private Long utilisateurId;
    private Long alimentId;
    private String nomAliment;
    
    private List<AllergieRisque> allergiesDetectees;
    private List<ReactionHistorique> reactionsPassees;
    private List<String> recommandations;
    
    private AnalyseIA analyseIA;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AllergieRisque {
        private Long allergieId;
        private String nomAllergie;
        private String niveauSeverite;
        private java.math.BigDecimal probabiliteReaction;
        private String typeAllergie;
        private boolean critique;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReactionHistorique {
        private java.time.LocalDateTime dateReaction;
        private String niveauSeverite;
        private String symptomes;
        private java.math.BigDecimal probabiliteAllergie;
        private boolean hospitalisationRequise;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnalyseIA {
        private java.math.BigDecimal probabiliteAllergieGlobale;
        private String raisonnement;
        private List<String> facteursRisque;
        private List<String> mesuresPrevention;
        private boolean consultationMedicaleRecommandee;
        private String seuillDetection; // Ex: ">30% de probabilité"
    }
}
