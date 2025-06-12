package com.tp222.allergies.repository;

import com.tp222.allergies.model.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Categorie
 */
@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    
    /**
     * Trouve une catégorie par son nom
     */
    Optional<Categorie> findByNom(String nom);
    
    /**
     * Trouve toutes les catégories actives
     */
    List<Categorie> findByActifTrue();
    
    /**
     * Recherche par nom (insensible à la casse)
     */
    @Query("SELECT c FROM Categorie c WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%')) AND c.actif = true")
    List<Categorie> findByNomContainingIgnoreCase(@Param("nom") String nom);
    
    /**
     * Compte le nombre d'aliments par catégorie
     */
    @Query("SELECT c, COUNT(a) FROM Categorie c LEFT JOIN c.aliments a WHERE c.actif = true GROUP BY c")
    List<Object[]> countAlimentsByCategory();
    
    /**
     * Trouve les catégories avec le plus d'aliments
     */
    @Query("SELECT c FROM Categorie c LEFT JOIN c.aliments a WHERE c.actif = true GROUP BY c ORDER BY COUNT(a) DESC")
    List<Categorie> findCategoriesOrderedByAlimentCount();
}
