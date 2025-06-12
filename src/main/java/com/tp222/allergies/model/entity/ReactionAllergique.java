package com.tp222.allergies.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Entité représentant une réaction allergique d'un utilisateur à un aliment
 */
@Entity
@Table(name = "reactions_allergiques")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionAllergique {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aliment_id", nullable = false)
    private Aliment aliment;
    
    @Column(name = "date_reaction", nullable = false)
    @NotNull(message = "La date de réaction est obligatoire")
    private LocalDateTime dateReaction;
    
    @Column(name = "niveau_severite")
    @Pattern(regexp = "^(LEGER|MODERE|SEVERE|CRITIQUE)$", message = "Niveau de sévérité invalide")
    private String niveauSeverite;
    
    @Column(name = "symptomes", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Les symptômes ne peuvent pas dépasser 1000 caractères")
    private String symptomes;
    
    @Column(name = "duree_reaction")
    @Min(value = 1, message = "La durée doit être positive")
    private Integer dureeReaction; // en minutes
    
    @Column(name = "quantite_consommee")
    @Min(value = 0, message = "La quantité doit être positive")
    private BigDecimal quantiteConsommee; // en grammes
    
    @Column(name = "traitement_utilise", columnDefinition = "TEXT")
    @Size(max = 500, message = "Le traitement utilisé ne peut pas dépasser 500 caractères")
    private String traitementUtilise;
    
    @Column(name = "contexte_consommation", columnDefinition = "TEXT")
    @Size(max = 500, message = "Le contexte ne peut pas dépasser 500 caractères")
    private String contexteConsommation;
    
    @Column(name = "lieu_reaction")
    @Size(max = 100, message = "Le lieu ne peut pas dépasser 100 caractères")
    private String lieuReaction;
    
    @Column(name = "medecin_consulte")
    private Boolean medecinConsulte;
    
    @Column(name = "hospitalisation_requise")
    private Boolean hospitalisationRequise;
    
    @Column(name = "probabilite_allergie")
    @DecimalMin(value = "0.0", message = "La probabilité doit être entre 0 et 1")
    @DecimalMax(value = "1.0", message = "La probabilité doit être entre 0 et 1")
    private BigDecimal probabiliteAllergie;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères")
    private String notes;
    
    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;
    
    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    /**
     * Détermine si la réaction indique une allergie probable (>30% de probabilité)
     * @return true si allergie probable, false sinon
     */
    public boolean isAllergic() {
        return probabiliteAllergie != null && 
               probabiliteAllergie.compareTo(BigDecimal.valueOf(0.3)) > 0;
    }
    
    /**
     * Vérifie si la réaction est critique
     * @return true si critique, false sinon
     */
    public boolean isCritique() {
        return "CRITIQUE".equals(niveauSeverite) || hospitalisationRequise == Boolean.TRUE;
    }
    
    /**
     * Vérifie si la réaction a nécessité une intervention médicale
     * @return true si intervention médicale, false sinon
     */
    public boolean hasRequiredMedicalIntervention() {
        return medecinConsulte == Boolean.TRUE || hospitalisationRequise == Boolean.TRUE;
    }
    
    /**
     * Calcule un score de risque basé sur plusieurs facteurs
     * @return score de risque entre 0 et 100
     */
    public int calculateRiskScore() {
        int score = 0;
        
        // Score basé sur la sévérité
        if ("CRITIQUE".equals(niveauSeverite)) score += 40;
        else if ("SEVERE".equals(niveauSeverite)) score += 30;
        else if ("MODERE".equals(niveauSeverite)) score += 20;
        else if ("LEGER".equals(niveauSeverite)) score += 10;
        
        // Score basé sur la probabilité d'allergie
        if (probabiliteAllergie != null) {
            score += probabiliteAllergie.multiply(BigDecimal.valueOf(30)).intValue();
        }
        
        // Score basé sur l'intervention médicale
        if (hospitalisationRequise == Boolean.TRUE) score += 20;
        else if (medecinConsulte == Boolean.TRUE) score += 10;
        
        // Score basé sur la durée de réaction
        if (dureeReaction != null) {
            if (dureeReaction > 240) score += 10; // Plus de 4 heures
            else if (dureeReaction > 60) score += 5; // Plus d'1 heure
        }
        
        return Math.min(score, 100);
    }
}
