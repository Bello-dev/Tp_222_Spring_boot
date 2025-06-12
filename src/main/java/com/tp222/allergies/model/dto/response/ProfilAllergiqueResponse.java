package com.tp222.allergies.model.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour la réponse du profil allergique d'un utilisateur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfilAllergiqueResponse {
    
    private Long utilisateurId;
    private String nomUtilisateur;
    private String prenomUtilisateur;
    private String emailUtilisateur;
    
    private List<AllergieInfo> allergies;
    private List<ReactionRecenteInfo> reactionsRecentes;
    
    private StatistiquesAllergie statistiques;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AllergieInfo {
        private Long allergieId;
        private String nomAllergie;
        private String typeAllergie;
        private String niveauSeverite;
        private LocalDateTime dateDiagnostic;
        private String diagnostiquePar;
        private String notes;
        private boolean actif;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReactionRecenteInfo {
        private Long reactionId;
        private String nomAliment;
        private LocalDateTime dateReaction;
        private String niveauSeverite;
        private String symptomes;
        private boolean hospitalisationRequise;
        private java.math.BigDecimal probabiliteAllergie;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatistiquesAllergie {
        private int nombreAllergies;
        private int nombreAllergiesCritiques;
        private int nombreReactionsTotal;
        private int nombreReactionsDernierMois;
        private String allergieRecurrente;
        private String alimentRisque;
        private int scoreRisqueGlobal;
    }
}
