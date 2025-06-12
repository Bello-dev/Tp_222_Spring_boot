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
 * Entité représentant une allergie
 */
@Entity
@Table(name = "allergies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Allergie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Le nom de l'allergie est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;
    
    @Column(name = "niveau_severite")
    @Pattern(regexp = "^(LEGER|MODERE|SEVERE|CRITIQUE)$", message = "Niveau de sévérité invalide")
    private String niveauSeverite;
    
    @Column(name = "type_allergie")
    @Pattern(regexp = "^(ALIMENTAIRE|RESPIRATOIRE|CUTANEE|MEDICAMENTEUSE|AUTRE)$", message = "Type d'allergie invalide")
    private String typeAllergie;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 500, message = "Les symptômes ne peuvent pas dépasser 500 caractères")
    private String symptomes;
    
    @Column(name = "traitement_recommande", columnDefinition = "TEXT")
    @Size(max = 500, message = "Le traitement recommandé ne peut pas dépasser 500 caractères")
    private String traitementRecommande;
    
    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;
    
    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;
    
    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    // Relations
    @OneToMany(mappedBy = "allergie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<AllergieUtilisateur> utilisateurs;
    
    /**
     * Détermine si l'allergie est critique
     * @return true si critique, false sinon
     */
    public boolean isCritique() {
        return "CRITIQUE".equals(niveauSeverite);
    }
    
    /**
     * Vérifie si l'allergie est alimentaire
     * @return true si alimentaire, false sinon
     */
    public boolean isAlimentaire() {
        return "ALIMENTAIRE".equals(typeAllergie);
    }
}
