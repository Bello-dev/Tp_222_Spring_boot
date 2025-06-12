package com.tp222.allergies.repository;

import com.tp222.allergies.model.entity.Allergie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Allergie
 */
@Repository
public interface AllergieRepository extends JpaRepository<Allergie, Long> {
    
    /**
     * Trouve une allergie par son nom
     */
    Optional<Allergie> findByNom(String nom);
    
    /**
     * Trouve toutes les allergies actives
     */
    List<Allergie> findByActifTrue();
    
    /**
     * Trouve les allergies par type
     */
    List<Allergie> findByTypeAllergie(String typeAllergie);
    
    /**
     * Trouve les allergies par niveau de sévérité
     */
    List<Allergie> findByNiveauSeverite(String niveauSeverite);
    
    /**
     * Trouve les allergies critiques
     */
    @Query("SELECT a FROM Allergie a WHERE a.niveauSeverite = 'CRITIQUE' AND a.actif = true")
    List<Allergie> findCriticalAllergies();
    
    /**
     * Trouve les allergies alimentaires
     */
    @Query("SELECT a FROM Allergie a WHERE a.typeAllergie = 'ALIMENTAIRE' AND a.actif = true")
    List<Allergie> findFoodAllergies();
    
    /**
     * Recherche par nom (insensible à la casse)
     */
    @Query("SELECT a FROM Allergie a WHERE LOWER(a.nom) LIKE LOWER(CONCAT('%', :nom, '%')) AND a.actif = true")
    List<Allergie> findByNomContainingIgnoreCase(@Param("nom") String nom);
    
    /**
     * Compte le nombre d'allergies par type
     */
    @Query("SELECT a.typeAllergie, COUNT(a) FROM Allergie a WHERE a.actif = true GROUP BY a.typeAllergie")
    List<Object[]> countByType();
    
    /**
     * Trouve les allergies les plus communes (par nombre d'utilisateurs)
     */
    @Query("SELECT a, COUNT(au) as userCount FROM Allergie a " +
           "LEFT JOIN a.utilisateurs au " +
           "WHERE a.actif = true " +
           "GROUP BY a " +
           "ORDER BY userCount DESC")
    List<Object[]> findMostCommonAllergies();
}
