package com.tp222.allergies.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * DTO pour la création/modification d'une réaction allergique
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionAllergiqueRequest {
    
    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long utilisateurId;
    
    @NotNull(message = "L'ID de l'aliment est obligatoire")
    private Long alimentId;
    
    @NotNull(message = "La date de réaction est obligatoire")
    private LocalDateTime dateReaction;
    
    @Pattern(regexp = "^(LEGER|MODERE|SEVERE|CRITIQUE)$", message = "Niveau de sévérité invalide")
    private String niveauSeverite;
    
    @Size(max = 1000, message = "Les symptômes ne peuvent pas dépasser 1000 caractères")
    private String symptomes;
    
    @Min(value = 1, message = "La durée doit être positive")
    private Integer dureeReaction; // en minutes
    
    @DecimalMin(value = "0.0", message = "La quantité doit être positive")
    private BigDecimal quantiteConsommee; // en grammes
    
    @Size(max = 500, message = "Le traitement utilisé ne peut pas dépasser 500 caractères")
    private String traitementUtilise;
    
    @Size(max = 500, message = "Le contexte ne peut pas dépasser 500 caractères")
    private String contexteConsommation;
    
    @Size(max = 100, message = "Le lieu ne peut pas dépasser 100 caractères")
    private String lieuReaction;
    
    private Boolean medecinConsulte;
    
    private Boolean hospitalisationRequise;
    
    @DecimalMin(value = "0.0", message = "La probabilité doit être entre 0 et 1")
    @DecimalMax(value = "1.0", message = "La probabilité doit être entre 0 et 1")
    private BigDecimal probabiliteAllergie;
    
    @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères")
    private String notes;
}
