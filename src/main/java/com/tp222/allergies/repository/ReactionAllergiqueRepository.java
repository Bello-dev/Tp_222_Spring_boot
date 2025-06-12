package com.tp222.allergies.repository;

import com.tp222.allergies.model.entity.ReactionAllergique;
import com.tp222.allergies.model.entity.Utilisateur;
import com.tp222.allergies.model.entity.Aliment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

/**
 * Repository pour l'entité ReactionAllergique
 */
@Repository
public interface ReactionAllergiqueRepository extends JpaRepository<ReactionAllergique, Long> {
    
    /**
     * Trouve toutes les réactions d'un utilisateur
     */
    List<ReactionAllergique> findByUtilisateur(Utilisateur utilisateur);
    
    /**
     * Trouve toutes les réactions à un aliment
     */
    List<ReactionAllergique> findByAliment(Aliment aliment);
    
    /**
     * Trouve les réactions d'un utilisateur pour un aliment spécifique
     */
    List<ReactionAllergique> findByUtilisateurAndAliment(Utilisateur utilisateur, Aliment aliment);
    
    /**
     * Trouve les réactions par niveau de sévérité
     */
    List<ReactionAllergique> findByNiveauSeverite(String niveauSeverite);
    
    /**
     * Trouve les réactions critiques
     */
    @Query("SELECT r FROM ReactionAllergique r WHERE r.niveauSeverite = 'CRITIQUE' OR r.hospitalisationRequise = true")
    List<ReactionAllergique> findCriticalReactions();
    
    /**
     * Trouve les réactions avec probabilité d'allergie élevée (>30%)
     */
    @Query("SELECT r FROM ReactionAllergique r WHERE r.probabiliteAllergie > 0.3")
    List<ReactionAllergique> findHighProbabilityAllergicReactions();
    
    /**
     * Trouve les réactions récentes d'un utilisateur
     */
    @Query("SELECT r FROM ReactionAllergique r WHERE r.utilisateur = :utilisateur AND r.dateReaction >= :dateDebut ORDER BY r.dateReaction DESC")
    List<ReactionAllergique> findRecentReactionsByUser(@Param("utilisateur") Utilisateur utilisateur, @Param("dateDebut") LocalDateTime dateDebut);
    
    /**
     * Analyse des tendances - réactions par aliment
     */
    @Query("SELECT r.aliment, COUNT(r), AVG(r.probabiliteAllergie) FROM ReactionAllergique r GROUP BY r.aliment ORDER BY COUNT(r) DESC")
    List<Object[]> getReactionTrendsByFood();
    
    /**
     * Trouve les aliments les plus problématiques
     */
    @Query("SELECT r.aliment, COUNT(r) as reactionCount FROM ReactionAllergique r WHERE r.probabiliteAllergie > 0.3 GROUP BY r.aliment ORDER BY reactionCount DESC")
    List<Object[]> getMostProblematicFoods();
    
    /**
     * Statistiques des réactions par mois
     */
    @Query("SELECT YEAR(r.dateReaction), MONTH(r.dateReaction), COUNT(r) FROM ReactionAllergique r GROUP BY YEAR(r.dateReaction), MONTH(r.dateReaction) ORDER BY YEAR(r.dateReaction), MONTH(r.dateReaction)")
    List<Object[]> getReactionStatsByMonth();
    
    /**
     * Trouve les utilisateurs avec le plus de réactions
     */
    @Query("SELECT r.utilisateur, COUNT(r) as reactionCount FROM ReactionAllergique r GROUP BY r.utilisateur ORDER BY reactionCount DESC")
    List<Object[]> getUsersWithMostReactions();
    
    /**
     * Analyse de la corrélation quantité-réaction
     */
    @Query("SELECT r FROM ReactionAllergique r WHERE r.quantiteConsommee IS NOT NULL AND r.probabiliteAllergie IS NOT NULL ORDER BY r.quantiteConsommee")
    List<ReactionAllergique> getQuantityCorrelationData();
    
    /**
     * Score de risque moyen par aliment
     */
    @Query("SELECT r.aliment, AVG(" +
           "CASE " +
           "WHEN r.niveauSeverite = 'CRITIQUE' THEN 40 " +
           "WHEN r.niveauSeverite = 'SEVERE' THEN 30 " +
           "WHEN r.niveauSeverite = 'MODERE' THEN 20 " +
           "WHEN r.niveauSeverite = 'LEGER' THEN 10 " +
           "ELSE 0 END + " +
           "COALESCE(r.probabiliteAllergie * 30, 0) + " +
           "CASE WHEN r.hospitalisationRequise = true THEN 20 WHEN r.medecinConsulte = true THEN 10 ELSE 0 END" +
           ") as avgRiskScore " +
           "FROM ReactionAllergique r " +
           "GROUP BY r.aliment " +
           "ORDER BY avgRiskScore DESC")
    List<Object[]> getAverageRiskScoreByFood();
}
