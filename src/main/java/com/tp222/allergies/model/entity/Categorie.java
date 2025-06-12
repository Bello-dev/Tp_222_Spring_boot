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
 * Entité représentant une catégorie d'aliments
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categorie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;
    
    @Column(name = "couleur_affichage")
    @Size(max = 7, message = "La couleur doit être un code hexadécimal valide")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Format de couleur invalide (ex: #FF5733)")
    private String couleurAffichage;
    
    @Column(name = "icone")
    @Size(max = 50, message = "L'icône ne peut pas dépasser 50 caractères")
    private String icone;
    
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
    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<Aliment> aliments;
}
