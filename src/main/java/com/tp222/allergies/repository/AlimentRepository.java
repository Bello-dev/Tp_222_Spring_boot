package com.tp222.allergies.repository;

import com.tp222.allergies.model.entity.Aliment;
import com.tp222.allergies.model.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Aliment
 */
@Repository
public interface AlimentRepository extends JpaRepository<Aliment, Long> {
    
    /**
     * Trouve un aliment par son nom
     */
    Optional<Aliment> findByNom(String nom);
    
    /**
     * Trouve tous les aliments actifs
     */
    List<Aliment> findByActifTrue();
    
    /**
     * Trouve les aliments par catégorie
     */
    List<Aliment> findByCategorie(Categorie categorie);
    
    /**
     * Trouve les aliments par nom (recherche insensible à la casse)
     */
    @Query("SELECT a FROM Aliment a WHERE LOWER(a.nom) LIKE LOWER(CONCAT('%', :nom, '%')) AND a.actif = true")
    List<Aliment> findByNomContainingIgnoreCase(@Param("nom") String nom);
    
    /**
     * Trouve les aliments riches en protéines (>20g/100g)
     */
    @Query("SELECT a FROM Aliment a WHERE a.proteines > 20 AND a.actif = true")
    List<Aliment> findHighProteinFoods();
    
    /**
     * Trouve les aliments faibles en calories (<100 cal/100g)
     */
    @Query("SELECT a FROM Aliment a WHERE a.calories < 100 AND a.actif = true")
    List<Aliment> findLowCalorieFoods();
    
    /**
     * Trouve les aliments par score nutritionnel minimum
     */
    @Query("SELECT a FROM Aliment a WHERE a.scoreNutritionnel >= :scoreMin AND a.actif = true ORDER BY a.scoreNutritionnel DESC")
    List<Aliment> findByScoreNutritionnelGreaterThanEqual(@Param("scoreMin") Integer scoreMin);
    
    /**
     * Trouve les aliments sans allergènes
     */
    @Query("SELECT a FROM Aliment a WHERE a.allergenesPotentiels IS NULL OR a.allergenesPotentiels = '' AND a.actif = true")
    List<Aliment> findAllergenFreeFoods();
    
    /**
     * Trouve les aliments par plage de calories
     */
    @Query("SELECT a FROM Aliment a WHERE a.calories BETWEEN :caloriesMin AND :caloriesMax AND a.actif = true")
    List<Aliment> findByCalorieRange(@Param("caloriesMin") BigDecimal caloriesMin, @Param("caloriesMax") BigDecimal caloriesMax);
    
    /**
     * Trouve les aliments par saison
     */
    @Query("SELECT a FROM Aliment a WHERE LOWER(a.saisonDisponibilite) LIKE LOWER(CONCAT('%', :saison, '%')) AND a.actif = true")
    List<Aliment> findBySaison(@Param("saison") String saison);
    
    /**
     * Trouve les aliments recommandés (score nutritionnel élevé et faibles allergènes)
     */
    @Query("SELECT a FROM Aliment a WHERE a.scoreNutritionnel >= 70 AND " +
           "(a.allergenesPotentiels IS NULL OR a.allergenesPotentiels = '') AND a.actif = true " +
           "ORDER BY a.scoreNutritionnel DESC")
    List<Aliment> findRecommendedFoods();
    
    /**
     * Statistiques nutritionnelles par catégorie
     */
    @Query("SELECT c.nom, AVG(a.calories), AVG(a.proteines), AVG(a.glucides), AVG(a.lipides) " +
           "FROM Aliment a JOIN a.categorie c WHERE a.actif = true GROUP BY c.nom")
    List<Object[]> getNutritionalStatsByCategory();
}
