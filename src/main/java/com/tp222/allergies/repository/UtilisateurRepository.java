package com.tp222.allergies.repository;

import com.tp222.allergies.model.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Utilisateur
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    
    /**
     * Trouve un utilisateur par son nom d'utilisateur
     */
    Optional<Utilisateur> findByUsername(String username);
    
    /**
     * Trouve un utilisateur par son email
     */
    Optional<Utilisateur> findByEmail(String email);
    
    /**
     * Vérifie si un nom d'utilisateur existe déjà
     */
    boolean existsByUsername(String username);
    
    /**
     * Vérifie si un email existe déjà
     */
    boolean existsByEmail(String email);
    
    /**
     * Trouve tous les utilisateurs actifs
     */
    List<Utilisateur> findByActifTrue();
    
    /**
     * Trouve les utilisateurs par prénom et nom
     */
    @Query("SELECT u FROM Utilisateur u WHERE " +
           "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :prenom, '%')) AND " +
           "LOWER(u.nom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    List<Utilisateur> findByPrenomAndNomContainingIgnoreCase(
        @Param("prenom") String prenom, 
        @Param("nom") String nom
    );
    
    /**
     * Trouve les utilisateurs par tranche d'âge
     */
    @Query("SELECT u FROM Utilisateur u WHERE " +
           "u.dateNaissance BETWEEN :dateNaissanceDebut AND :dateNaissanceFin")
    List<Utilisateur> findByAgeRange(
        @Param("dateNaissanceDebut") LocalDate dateNaissanceDebut,
        @Param("dateNaissanceFin") LocalDate dateNaissanceFin
    );
    
    /**
     * Trouve les utilisateurs ayant des allergies critiques
     */
    @Query("SELECT DISTINCT u FROM Utilisateur u " +
           "JOIN u.allergies au " +
           "WHERE au.niveauSeverite = 'CRITIQUE' AND u.actif = true")
    List<Utilisateur> findUsersWithCriticalAllergies();
    
    /**
     * Trouve les utilisateurs par niveau d'activité
     */
    List<Utilisateur> findByNiveauActivite(String niveauActivite);
    
    /**
     * Compte le nombre d'utilisateurs actifs
     */
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.actif = true")
    long countActiveUsers();
    
    /**
     * Trouve les utilisateurs créés après une certaine date
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.dateCreation >= :dateCreation")
    List<Utilisateur> findRecentUsers(@Param("dateCreation") java.time.LocalDateTime dateCreation);
}
