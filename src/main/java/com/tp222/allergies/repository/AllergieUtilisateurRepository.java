package com.tp222.allergies.repository;

import com.tp222.allergies.model.entity.AllergieUtilisateur;
import com.tp222.allergies.model.entity.AllergieUtilisateur.AllergieUtilisateurId;
import com.tp222.allergies.model.entity.Utilisateur;
import com.tp222.allergies.model.entity.Allergie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité AllergieUtilisateur
 */
@Repository
public interface AllergieUtilisateurRepository extends JpaRepository<AllergieUtilisateur, AllergieUtilisateurId> {
    
    /**
     * Trouve toutes les allergies d'un utilisateur
     */
    List<AllergieUtilisateur> findByUtilisateur(Utilisateur utilisateur);
    
    /**
     * Trouve tous les utilisateurs ayant une allergie spécifique
     */
    List<AllergieUtilisateur> findByAllergie(Allergie allergie);
    
    /**
     * Trouve les allergies d'un utilisateur par sévérité
     */
    Optional<AllergieUtilisateur> findByUtilisateurAndAllergie(Utilisateur utilisateur, Allergie allergie);
    
    /**
     * Trouve les allergies actives d'un utilisateur
     */
    List<AllergieUtilisateur> findByUtilisateurAndActifTrue(Utilisateur utilisateur);
    
    /**
     * Trouve les allergies critiques d'un utilisateur
     */
    List<AllergieUtilisateur> findByUtilisateurAndNiveauSeverite(Utilisateur utilisateur, String niveauSeverite);
    
    /**
     * Compte le nombre d'allergies par utilisateur
     */
    @Query("SELECT au.utilisateur, COUNT(au) FROM AllergieUtilisateur au WHERE au.actif = true GROUP BY au.utilisateur")
    List<Object[]> countAllergiesByUser();
    
    /**
     * Trouve les utilisateurs avec des allergies multiples
     */
    @Query("SELECT au.utilisateur FROM AllergieUtilisateur au WHERE au.actif = true GROUP BY au.utilisateur HAVING COUNT(au) > 1")
    List<Utilisateur> findUsersWithMultipleAllergies();
    
    /**
     * Trouve les allergies les plus fréquentes
     */
    @Query("SELECT au.allergie, COUNT(au) as frequency FROM AllergieUtilisateur au WHERE au.actif = true GROUP BY au.allergie ORDER BY frequency DESC")
    List<Object[]> findMostFrequentAllergies();
    
    /**
     * Vérifie si un utilisateur a une allergie spécifique
     */
    @Query("SELECT COUNT(au) > 0 FROM AllergieUtilisateur au WHERE au.utilisateur = :utilisateur AND au.allergie = :allergie AND au.actif = true")
    boolean userHasAllergie(@Param("utilisateur") Utilisateur utilisateur, @Param("allergie") Allergie allergie);
    
    /**
     * Trouve les allergies critiques par utilisateur
     */
    @Query("SELECT au FROM AllergieUtilisateur au WHERE au.utilisateur = :utilisateur AND au.niveauSeverite = 'CRITIQUE' AND au.actif = true")
    List<AllergieUtilisateur> findCriticalAllergiesByUser(@Param("utilisateur") Utilisateur utilisateur);
    
    /**
     * Statistiques des allergies par sévérité
     */
    @Query("SELECT au.niveauSeverite, COUNT(au) FROM AllergieUtilisateur au WHERE au.actif = true GROUP BY au.niveauSeverite")
    List<Object[]> getAllergieStatsBySeverity();
}
