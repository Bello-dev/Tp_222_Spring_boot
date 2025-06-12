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
 * Entité représentant un aliment
 */
@Entity
@Table(name = "aliments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aliment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotBlank(message = "Le nom de l'aliment est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;
    
    // Informations nutritionnelles pour 100g
    @Column(name = "calories")
    @Min(value = 0, message = "Les calories doivent être positives")
    private BigDecimal calories;
    
    @Column(name = "proteines")
    @Min(value = 0, message = "Les protéines doivent être positives")
    private BigDecimal proteines;
    
    @Column(name = "glucides")
    @Min(value = 0, message = "Les glucides doivent être positifs")
    private BigDecimal glucides;
    
    @Column(name = "lipides")
    @Min(value = 0, message = "Les lipides doivent être positifs")
    private BigDecimal lipides;
    
    @Column(name = "fibres")
    @Min(value = 0, message = "Les fibres doivent être positives")
    private BigDecimal fibres;
    
    @Column(name = "sodium")
    @Min(value = 0, message = "Le sodium doit être positif")
    private BigDecimal sodium;
    
    @Column(name = "calcium")
    @Min(value = 0, message = "Le calcium doit être positif")
    private BigDecimal calcium;
    
    @Column(name = "fer")
    @Min(value = 0, message = "Le fer doit être positif")
    private BigDecimal fer;
    
    @Column(name = "vitamine_c")
    @Min(value = 0, message = "La vitamine C doit être positive")
    private BigDecimal vitamineC;
    
    @Column(name = "score_nutritionnel")
    @Min(value = 0, message = "Le score nutritionnel doit être positif")
    @Max(value = 100, message = "Le score nutritionnel ne peut pas dépasser 100")
    private Integer scoreNutritionnel;
    
    @Column(name = "allergenes_potentiels", columnDefinition = "TEXT")
    @Size(max = 500, message = "Les allergènes potentiels ne peuvent pas dépasser 500 caractères")
    private String allergenesPotentiels;
    
    @Column(name = "saison_disponibilite")
    @Size(max = 100, message = "La saison de disponibilité ne peut pas dépasser 100 caractères")
    private String saisonDisponibilite;
    
    @Column(name = "origine_geographique")
    @Size(max = 100, message = "L'origine géographique ne peut pas dépasser 100 caractères")
    private String origineGeographique;
    
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
    @OneToMany(mappedBy = "aliment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<ReactionAllergique> reactions;
    
    /**
     * Calcule la densité énergétique (calories/100g)
     * @return densité énergétique ou null si calories manquantes
     */
    public BigDecimal getDensiteEnergetique() {
        return calories;
    }
    
    /**
     * Vérifie si l'aliment est riche en protéines (>20g/100g)
     * @return true si riche en protéines, false sinon
     */
    public boolean isRicheEnProteines() {
        return proteines != null && proteines.compareTo(BigDecimal.valueOf(20)) > 0;
    }
    
    /**
     * Vérifie si l'aliment est faible en calories (<100 cal/100g)
     * @return true si faible en calories, false sinon
     */
    public boolean isFaibleEnCalories() {
        return calories != null && calories.compareTo(BigDecimal.valueOf(100)) < 0;
    }
    
    /**
     * Vérifie si l'aliment contient des allergènes potentiels
     * @return true si contient des allergènes, false sinon
     */
    public boolean containsAllergenes() {
        return allergenesPotentiels != null && !allergenesPotentiels.trim().isEmpty();
    }
}
