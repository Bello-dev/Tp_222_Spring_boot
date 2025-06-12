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
import java.util.List;

/**
 * Entité représentant une recette
 */
@Entity
@Table(name = "recettes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recette {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotBlank(message = "Le nom de la recette est obligatoire")
    @Size(max = 200, message = "Le nom ne peut pas dépasser 200 caractères")
    private String nom;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    private String description;
    
    @Column(name = "instructions", columnDefinition = "TEXT")
    @NotBlank(message = "Les instructions sont obligatoires")
    @Size(max = 5000, message = "Les instructions ne peuvent pas dépasser 5000 caractères")
    private String instructions;
    
    @Column(name = "temps_preparation")
    @Min(value = 1, message = "Le temps de préparation doit être positif")
    private Integer tempsPreparation; // en minutes
    
    @Column(name = "temps_cuisson")
    @Min(value = 0, message = "Le temps de cuisson doit être positif ou nul")
    private Integer tempsCuisson; // en minutes
    
    @Column(name = "nombre_portions")
    @Min(value = 1, message = "Le nombre de portions doit être au moins 1")
    private Integer nombrePortions;
    
    @Column(name = "niveau_difficulte")
    @Pattern(regexp = "^(FACILE|MOYEN|DIFFICILE)$", message = "Niveau de difficulté invalide")
    private String niveauDifficulte;
    
    @Column(name = "type_cuisine")
    @Size(max = 50, message = "Le type de cuisine ne peut pas dépasser 50 caractères")
    private String typeCuisine;
    
    @Column(name = "calories_par_portion")
    @Min(value = 0, message = "Les calories doivent être positives")
    private BigDecimal caloriesParPortion;
    
    @Column(name = "ingredients", columnDefinition = "TEXT")
    @NotBlank(message = "La liste des ingrédients est obligatoire")
    @Size(max = 2000, message = "Les ingrédients ne peuvent pas dépasser 2000 caractères")
    private String ingredients;
    
    @Column(name = "allergenes_presents", columnDefinition = "TEXT")
    @Size(max = 500, message = "Les allergènes ne peuvent pas dépasser 500 caractères")
    private String allergenesPresents;
    
    @Column(name = "image_url")
    @Size(max = 255, message = "L'URL de l'image ne peut pas dépasser 255 caractères")
    private String imageUrl;
    
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
     * Calcule le temps total de préparation
     * @return temps total en minutes
     */
    public Integer getTempsTotal() {
        int total = 0;
        if (tempsPreparation != null) total += tempsPreparation;
        if (tempsCuisson != null) total += tempsCuisson;
        return total;
    }
    
    /**
     * Vérifie si la recette est rapide (moins de 30 minutes)
     * @return true si rapide, false sinon
     */
    public boolean isRapide() {
        return getTempsTotal() <= 30;
    }
    
    /**
     * Vérifie si la recette contient des allergènes
     * @return true si contient des allergènes, false sinon
     */
    public boolean containsAllergenes() {
        return allergenesPresents != null && !allergenesPresents.trim().isEmpty();
    }
    
    /**
     * Vérifie si la recette est adaptée à un utilisateur (pas d'allergènes problématiques)
     * @param userAllergies liste des allergies de l'utilisateur
     * @return true si adaptée, false sinon
     */
    public boolean isSafeForUser(List<String> userAllergies) {
        if (userAllergies == null || userAllergies.isEmpty()) return true;
        if (allergenesPresents == null || allergenesPresents.trim().isEmpty()) return true;
        
        String allergenesLower = allergenesPresents.toLowerCase();
        return userAllergies.stream()
                .noneMatch(allergie -> allergenesLower.contains(allergie.toLowerCase()));
    }
}
