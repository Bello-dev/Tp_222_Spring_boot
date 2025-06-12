package com.tp222.allergies.controller;

import com.tp222.allergies.model.dto.request.ReactionAllergiqueRequest;
import com.tp222.allergies.model.dto.response.ProfilAllergiqueResponse;
import com.tp222.allergies.model.dto.response.VerificationRisqueResponse;
import com.tp222.allergies.model.entity.ReactionAllergique;
import com.tp222.allergies.service.AllergieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour la gestion des allergies avec IA
 */
@RestController
@RequestMapping("/allergies")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gestion des Allergies", description = "API pour la gestion intelligente des allergies avec détection automatique")
public class AllergieController {
    
    private final AllergieService allergieService;
    
    @PostMapping("/reactions")
    @Operation(
        summary = "Enregistrer une réaction allergique",
        description = "Enregistre une nouvelle réaction allergique avec analyse IA automatique. " +
                     "Le système détecte automatiquement les allergies probables (>30% de probabilité)."
    )
    @ApiResponse(responseCode = "201", description = "Réaction enregistrée avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Utilisateur ou aliment non trouvé")
    public ResponseEntity<ReactionAllergique> enregistrerReaction(
            @Valid @RequestBody ReactionAllergiqueRequest request) {
        
        log.info("Nouvelle demande d'enregistrement de réaction allergique: utilisateur={}, aliment={}", 
                request.getUtilisateurId(), request.getAlimentId());
        
        try {
            ReactionAllergique reaction = allergieService.enregistrerReaction(request);
            
            // Log spécial si allergie détectée automatiquement
            if (reaction.isAllergic()) {
                log.warn("ALLERGIE DÉTECTÉE AUTOMATIQUEMENT - Utilisateur: {}, Aliment: {}, Probabilité: {}%", 
                        request.getUtilisateurId(), request.getAlimentId(), 
                        reaction.getProbabiliteAllergie().multiply(java.math.BigDecimal.valueOf(100)));
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(reaction);
            
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'enregistrement de la réaction: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/verification-risque")
    @Operation(
        summary = "Vérifier le risque d'allergie",
        description = "Analyse intelligente du risque d'allergie pour un utilisateur et un aliment spécifique. " +
                     "Utilise l'IA pour évaluer les probabilités basées sur l'historique et les allergies connues."
    )
    @ApiResponse(responseCode = "200", description = "Analyse de risque effectuée")
    @ApiResponse(responseCode = "404", description = "Utilisateur ou aliment non trouvé")
    public ResponseEntity<VerificationRisqueResponse> verifierRisque(
            @Parameter(description = "ID de l'utilisateur") @RequestParam Long utilisateurId,
            @Parameter(description = "ID de l'aliment") @RequestParam Long alimentId) {
        
        log.info("Demande de vérification de risque: utilisateur={}, aliment={}", utilisateurId, alimentId);
        
        try {
            VerificationRisqueResponse response = allergieService.verifierRisque(utilisateurId, alimentId);
            
            // Log d'alerte si risque élevé détecté
            if ("CRITIQUE".equals(response.getNiveauRisque()) || "ELEVE".equals(response.getNiveauRisque())) {
                log.warn("RISQUE ÉLEVÉ DÉTECTÉ - Utilisateur: {}, Aliment: {}, Niveau: {}, Score: {}", 
                        utilisateurId, alimentId, response.getNiveauRisque(), response.getScoreRisque());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.error("Erreur lors de la vérification de risque: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/profil/{utilisateurId}")
    @Operation(
        summary = "Obtenir le profil allergique complet",
        description = "Récupère le profil allergique détaillé d'un utilisateur incluant ses allergies, " +
                     "réactions récentes et statistiques personnalisées."
    )
    @ApiResponse(responseCode = "200", description = "Profil allergique récupéré")
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    public ResponseEntity<ProfilAllergiqueResponse> getProfilAllergique(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long utilisateurId) {
        
        log.info("Demande de profil allergique pour utilisateur: {}", utilisateurId);
        
        try {
            ProfilAllergiqueResponse profil = allergieService.getProfilAllergique(utilisateurId);
            
            // Log informatif sur le profil
            log.info("Profil allergique récupéré - Utilisateur: {}, Allergies: {}, Réactions récentes: {}, Score risque: {}", 
                    utilisateurId, 
                    profil.getStatistiques().getNombreAllergies(),
                    profil.getStatistiques().getNombreReactionsDernierMois(),
                    profil.getStatistiques().getScoreRisqueGlobal());
            
            return ResponseEntity.ok(profil);
            
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récupération du profil allergique: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/detection-automatique/statut")
    @Operation(
        summary = "Statut de la détection automatique",
        description = "Retourne les informations sur le système de détection automatique d'allergies"
    )
    @ApiResponse(responseCode = "200", description = "Statut récupéré")
    public ResponseEntity<DetectionStatusResponse> getStatutDetectionAutomatique() {
        
        DetectionStatusResponse status = DetectionStatusResponse.builder()
                .detectionActive(true)
                .seuilDetection(30.0)
                .algorithmeVersion("v2.1")
                .description("Système IA de détection automatique des allergies basé sur l'analyse " +
                           "de la probabilité de réaction (seuil: >30%). Analyse multifactorielle " +
                           "incluant sévérité, historique, intervention médicale et composition alimentaire.")
                .facteursAnalyses(java.util.Arrays.asList(
                    "Niveau de sévérité de la réaction",
                    "Intervention médicale requise",
                    "Historique des réactions à l'aliment",
                    "Présence d'allergènes dans l'aliment",
                    "Profil allergique de l'utilisateur"
                ))
                .build();
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * DTO pour le statut de détection automatique
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DetectionStatusResponse {
        private boolean detectionActive;
        private double seuilDetection;
        private String algorithmeVersion;
        private String description;
        private java.util.List<String> facteursAnalyses;
    }
}
