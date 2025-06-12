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

/**
 * Entité de liaison entre Utilisateur et Allergie
 */
@Entity
@Table(name = "allergies_utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllergieUtilisateur {
    
    @EmbeddedId
    private AllergieUtilisateurId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("utilisateurId")
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("allergieId")
    @JoinColumn(name = "allergie_id")
    private Allergie allergie;
    
    @Column(name = "niveau_severite")
    @Pattern(regexp = "^(LEGER|MODERE|SEVERE|CRITIQUE)$", message = "Niveau de sévérité invalide")
    private String niveauSeverite;
    
    @Column(name = "date_diagnostic")
    private LocalDateTime dateDiagnostic;
    
    @Column(name = "diagnostique_par")
    @Size(max = 100, message = "Le diagnostiqueur ne peut pas dépasser 100 caractères")
    private String diagnostiquePar;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères")
    private String notes;
    
    @Column(name = "traitement_actuel", columnDefinition = "TEXT")
    @Size(max = 500, message = "Le traitement actuel ne peut pas dépasser 500 caractères")
    private String traitementActuel;
    
    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;
    
    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;
    
    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    /**
     * Classe pour la clé composée
     */
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllergieUtilisateurId implements java.io.Serializable {
        
        @Column(name = "utilisateur_id")
        private Long utilisateurId;
        
        @Column(name = "allergie_id")
        private Long allergieId;
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AllergieUtilisateurId)) return false;
            AllergieUtilisateurId that = (AllergieUtilisateurId) o;
            return utilisateurId.equals(that.utilisateurId) && allergieId.equals(that.allergieId);
        }
        
        @Override
        public int hashCode() {
            return java.util.Objects.hash(utilisateurId, allergieId);
        }
    }
    
    /**
     * Vérifie si l'allergie est critique pour cet utilisateur
     * @return true si critique, false sinon
     */
    public boolean isCritique() {
        return "CRITIQUE".equals(niveauSeverite);
    }
}
