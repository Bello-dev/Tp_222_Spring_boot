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
import java.time.LocalDate;
import java.util.List;

/**
 * Entité représentant un utilisateur du système de gestion des allergies
 */
@Entity
@Table(name = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;
    
    @Column(nullable = false, unique = true)
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;
    
    @Column(nullable = false)
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;
    
    @Column(nullable = false)
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères")
    private String prenom;
    
    @Column(nullable = false)
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
    private String nom;
    
    @Column(name = "date_naissance")
    private LocalDate dateNaissance;
    
    @Column(length = 10)
    @Pattern(regexp = "^(M|F)$", message = "Le sexe doit être M ou F")
    private String sexe;
    
    @Column(name = "poids")
    @Min(value = 1, message = "Le poids doit être positif")
    @Max(value = 500, message = "Le poids ne peut pas dépasser 500 kg")
    private Double poids;
    
    @Column(name = "taille")
    @Min(value = 50, message = "La taille doit être au moins 50 cm")
    @Max(value = 250, message = "La taille ne peut pas dépasser 250 cm")
    private Double taille;
    
    @Column(name = "niveau_activite")
    @Pattern(regexp = "^(SEDENTAIRE|LEGER|MODERE|INTENSE|TRES_INTENSE)$")
    private String niveauActivite;
    
    @Column(name = "objectif_sante")
    @Size(max = 255, message = "L'objectif santé ne peut pas dépasser 255 caractères")
    private String objectifSante;
    
    @Column(name = "numero_telephone")
    @Size(max = 20, message = "Le numéro de téléphone ne peut pas dépasser 20 caractères")
    private String numeroTelephone;
    
    @Column(name = "adresse")
    @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
    private String adresse;
    
    @Column(name = "medecin_traitant")
    @Size(max = 100, message = "Le nom du médecin traitant ne peut pas dépasser 100 caractères")
    private String medecinTraitant;
    
    @Column(name = "contact_urgence")
    @Size(max = 100, message = "Le contact d'urgence ne peut pas dépasser 100 caractères")
    private String contactUrgence;
    
    @Column(name = "telephone_urgence")
    @Size(max = 20, message = "Le téléphone d'urgence ne peut pas dépasser 20 caractères")
    private String telephoneUrgence;
    
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
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AllergieUtilisateur> allergies;
    
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReactionAllergique> reactions;
    
    /**
     * Calcule l'IMC (Indice de Masse Corporelle)
     * @return IMC ou null si poids ou taille manquant
     */
    public Double calculateIMC() {
        if (poids != null && taille != null && taille > 0) {
            double tailleEnMetres = taille / 100.0;
            return poids / (tailleEnMetres * tailleEnMetres);
        }
        return null;
    }
    
    /**
     * Vérifie si l'utilisateur est majeur
     * @return true si majeur, false sinon
     */
    public boolean isMajeur() {
        if (dateNaissance == null) return false;
        return LocalDate.now().minusYears(18).isAfter(dateNaissance) || 
               LocalDate.now().minusYears(18).isEqual(dateNaissance);
    }
    
    /**
     * Calcule l'âge de l'utilisateur
     * @return âge en années
     */
    public Integer getAge() {
        if (dateNaissance == null) return null;
        return LocalDate.now().getYear() - dateNaissance.getYear();
    }
}
