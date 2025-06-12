package com.tp222.allergies.data;

import com.tp222.allergies.model.entity.*;
import com.tp222.allergies.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Initialise la base de données avec des données de test
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DataSeeder implements CommandLineRunner {
    
    private final UtilisateurRepository utilisateurRepository;
    private final AllergieRepository allergieRepository;
    private final CategorieRepository categorieRepository;
    private final AlimentRepository alimentRepository;
    private final AllergieUtilisateurRepository allergieUtilisateurRepository;
    private final ReactionAllergiqueRepository reactionAllergiqueRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (utilisateurRepository.count() == 0) {
            log.info("Initialisation des données de test...");
            
            // Création des catégories d'aliments
            List<Categorie> categories = creerCategories();
            
            // Création des allergies
            List<Allergie> allergies = creerAllergies();
            
            // Création des aliments
            List<Aliment> aliments = creerAliments(categories);
            
            // Création des utilisateurs
            List<Utilisateur> utilisateurs = creerUtilisateurs();
            
            // Association allergies-utilisateurs
            creerAssociationsAllergies(utilisateurs, allergies);
            
            // Création de réactions de test
            creerReactionsTest(utilisateurs, aliments);
            
            log.info("Données de test initialisées avec succès !");
        }
    }
    
    private List<Categorie> creerCategories() {
        List<Categorie> categories = Arrays.asList(
            Categorie.builder()
                .nom("Fruits")
                .description("Fruits frais et secs")
                .couleurAffichage("#FF6B6B")
                .icone("🍎")
                .actif(true)
                .build(),
            
            Categorie.builder()
                .nom("Légumes")
                .description("Légumes frais et légumineuses")
                .couleurAffichage("#4ECDC4")
                .icone("🥕")
                .actif(true)
                .build(),
            
            Categorie.builder()
                .nom("Céréales")
                .description("Céréales et produits céréaliers")
                .couleurAffichage("#FFE66D")
                .icone("🌾")
                .actif(true)
                .build(),
            
            Categorie.builder()
                .nom("Protéines")
                .description("Viandes, poissons, œufs")
                .couleurAffichage("#A8E6CF")
                .icone("🥩")
                .actif(true)
                .build(),
            
            Categorie.builder()
                .nom("Produits laitiers")
                .description("Lait, fromages, yaourts")
                .couleurAffichage("#B4A7D6")
                .icone("🥛")
                .actif(true)
                .build()
        );
        
        return categorieRepository.saveAll(categories);
    }
    
    private List<Allergie> creerAllergies() {
        List<Allergie> allergies = Arrays.asList(
            Allergie.builder()
                .nom("Allergie aux arachides")
                .description("Allergie aux cacahuètes et produits dérivés")
                .niveauSeverite("CRITIQUE")
                .typeAllergie("ALIMENTAIRE")
                .symptomes("Urticaire, œdème, choc anaphylactique")
                .traitementRecommande("Éviction totale, auto-injecteur d'épinéphrine")
                .actif(true)
                .build(),
            
            Allergie.builder()
                .nom("Allergie aux œufs")
                .description("Allergie aux protéines d'œuf")
                .niveauSeverite("MODERE")
                .typeAllergie("ALIMENTAIRE")
                .symptomes("Éruptions cutanées, troubles digestifs")
                .traitementRecommande("Éviction, antihistaminiques")
                .actif(true)
                .build(),
            
            Allergie.builder()
                .nom("Allergie au lait de vache")
                .description("Intolérance aux protéines du lait")
                .niveauSeverite("MODERE")
                .typeAllergie("ALIMENTAIRE")
                .symptomes("Diarrhée, vomissements, eczéma")
                .traitementRecommande("Régime sans lactose")
                .actif(true)
                .build(),
            
            Allergie.builder()
                .nom("Allergie au gluten")
                .description("Maladie cœliaque")
                .niveauSeverite("SEVERE")
                .typeAllergie("ALIMENTAIRE")
                .symptomes("Troubles digestifs, fatigue, malabsorption")
                .traitementRecommande("Régime strictement sans gluten")
                .actif(true)
                .build(),
            
            Allergie.builder()
                .nom("Allergie aux fruits à coque")
                .description("Allergie aux noix, amandes, noisettes")
                .niveauSeverite("SEVERE")
                .typeAllergie("ALIMENTAIRE")
                .symptomes("Œdème, difficultés respiratoires")
                .traitementRecommande("Éviction, surveillance médicale")
                .actif(true)
                .build()
        );
        
        return allergieRepository.saveAll(allergies);
    }
    
    private List<Aliment> creerAliments(List<Categorie> categories) {
        Categorie fruits = categories.get(0);
        Categorie legumes = categories.get(1);
        Categorie cereales = categories.get(2);
        Categorie proteines = categories.get(3);
        Categorie laitiers = categories.get(4);
        
        List<Aliment> aliments = Arrays.asList(
            // Fruits
            Aliment.builder()
                .nom("Pomme")
                .description("Pomme rouge Golden")
                .categorie(fruits)
                .calories(BigDecimal.valueOf(52))
                .proteines(BigDecimal.valueOf(0.3))
                .glucides(BigDecimal.valueOf(14))
                .lipides(BigDecimal.valueOf(0.2))
                .fibres(BigDecimal.valueOf(2.4))
                .calcium(BigDecimal.valueOf(6))
                .fer(BigDecimal.valueOf(0.12))
                .vitamineC(BigDecimal.valueOf(4.6))
                .scoreNutritionnel(85)
                .allergenesPotentiels("")
                .saisonDisponibilite("Automne, Hiver")
                .origineGeographique("France")
                .actif(true)
                .build(),
            
            // Légumes
            Aliment.builder()
                .nom("Carotte")
                .description("Carotte fraîche")
                .categorie(legumes)
                .calories(BigDecimal.valueOf(41))
                .proteines(BigDecimal.valueOf(0.9))
                .glucides(BigDecimal.valueOf(9.6))
                .lipides(BigDecimal.valueOf(0.2))
                .fibres(BigDecimal.valueOf(2.8))
                .calcium(BigDecimal.valueOf(33))
                .fer(BigDecimal.valueOf(0.3))
                .vitamineC(BigDecimal.valueOf(5.9))
                .scoreNutritionnel(90)
                .allergenesPotentiels("")
                .saisonDisponibilite("Toute l'année")
                .origineGeographique("France")
                .actif(true)
                .build(),
            
            // Céréales
            Aliment.builder()
                .nom("Blé complet")
                .description("Farine de blé complète")
                .categorie(cereales)
                .calories(BigDecimal.valueOf(340))
                .proteines(BigDecimal.valueOf(13))
                .glucides(BigDecimal.valueOf(72))
                .lipides(BigDecimal.valueOf(2.5))
                .fibres(BigDecimal.valueOf(10.7))
                .calcium(BigDecimal.valueOf(34))
                .fer(BigDecimal.valueOf(3.6))
                .vitamineC(BigDecimal.valueOf(0))
                .scoreNutritionnel(75)
                .allergenesPotentiels("Gluten")
                .saisonDisponibilite("Toute l'année")
                .origineGeographique("France")
                .actif(true)
                .build(),
            
            // Protéines
            Aliment.builder()
                .nom("Œuf de poule")
                .description("Œuf frais de poule")
                .categorie(proteines)
                .calories(BigDecimal.valueOf(155))
                .proteines(BigDecimal.valueOf(13))
                .glucides(BigDecimal.valueOf(1.1))
                .lipides(BigDecimal.valueOf(11))
                .fibres(BigDecimal.valueOf(0))
                .calcium(BigDecimal.valueOf(56))
                .fer(BigDecimal.valueOf(1.8))
                .vitamineC(BigDecimal.valueOf(0))
                .scoreNutritionnel(80)
                .allergenesPotentiels("Œufs")
                .saisonDisponibilite("Toute l'année")
                .origineGeographique("France")
                .actif(true)
                .build(),
            
            // Produits laitiers
            Aliment.builder()
                .nom("Lait de vache")
                .description("Lait entier de vache")
                .categorie(laitiers)
                .calories(BigDecimal.valueOf(64))
                .proteines(BigDecimal.valueOf(3.2))
                .glucides(BigDecimal.valueOf(4.7))
                .lipides(BigDecimal.valueOf(3.6))
                .fibres(BigDecimal.valueOf(0))
                .calcium(BigDecimal.valueOf(113))
                .fer(BigDecimal.valueOf(0.1))
                .vitamineC(BigDecimal.valueOf(1))
                .scoreNutritionnel(70)
                .allergenesPotentiels("Lait, Lactose")
                .saisonDisponibilite("Toute l'année")
                .origineGeographique("France")
                .actif(true)
                .build(),
            
            // Aliment à risque
            Aliment.builder()
                .nom("Cacahuètes")
                .description("Arachides grillées")
                .categorie(proteines)
                .calories(BigDecimal.valueOf(567))
                .proteines(BigDecimal.valueOf(26))
                .glucides(BigDecimal.valueOf(16))
                .lipides(BigDecimal.valueOf(49))
                .fibres(BigDecimal.valueOf(8))
                .calcium(BigDecimal.valueOf(92))
                .fer(BigDecimal.valueOf(4.6))
                .vitamineC(BigDecimal.valueOf(0))
                .scoreNutritionnel(60)
                .allergenesPotentiels("Arachides, Fruits à coque")
                .saisonDisponibilite("Toute l'année")
                .origineGeographique("Import")
                .actif(true)
                .build()
        );
        
        return alimentRepository.saveAll(aliments);
    }
    
    private List<Utilisateur> creerUtilisateurs() {
        List<Utilisateur> utilisateurs = Arrays.asList(
            Utilisateur.builder()
                .username("marie.dupont")
                .email("marie.dupont@email.com")
                .motDePasse("password123") // En production, utiliser un hash
                .prenom("Marie")
                .nom("Dupont")
                .dateNaissance(LocalDate.of(1990, 5, 15))
                .sexe("F")
                .poids(65.0)
                .taille(170.0)
                .niveauActivite("MODERE")
                .objectifSante("Gestion des allergies alimentaires")
                .numeroTelephone("0123456789")
                .adresse("123 Rue de la République, 75001 Paris")
                .medecinTraitant("Dr. Martin")
                .contactUrgence("Pierre Dupont")
                .telephoneUrgence("0123456790")
                .actif(true)
                .build(),
            
            Utilisateur.builder()
                .username("jean.martin")
                .email("jean.martin@email.com")
                .motDePasse("password123")
                .prenom("Jean")
                .nom("Martin")
                .dateNaissance(LocalDate.of(1985, 8, 22))
                .sexe("M")
                .poids(80.0)
                .taille(180.0)
                .niveauActivite("INTENSE")
                .objectifSante("Prévention allergies")
                .numeroTelephone("0123456791")
                .adresse("456 Avenue des Champs, 69000 Lyon")
                .medecinTraitant("Dr. Leroy")
                .contactUrgence("Sophie Martin")
                .telephoneUrgence("0123456792")
                .actif(true)
                .build()
        );
        
        return utilisateurRepository.saveAll(utilisateurs);
    }
    
    private void creerAssociationsAllergies(List<Utilisateur> utilisateurs, List<Allergie> allergies) {
        Utilisateur marie = utilisateurs.get(0);
        Utilisateur jean = utilisateurs.get(1);
        
        // Marie a une allergie aux œufs
        AllergieUtilisateur marieOeufs = AllergieUtilisateur.builder()
                .id(new AllergieUtilisateur.AllergieUtilisateurId(marie.getId(), allergies.get(1).getId()))
                .utilisateur(marie)
                .allergie(allergies.get(1)) // Allergie aux œufs
                .niveauSeverite("MODERE")
                .dateDiagnostic(LocalDateTime.of(2023, 3, 15, 10, 0))
                .diagnostiquePar("Dr. Martin")
                .notes("Diagnostic confirmé par tests cutanés")
                .traitementActuel("Éviction des œufs, antihistaminiques en cas de contact")
                .actif(true)
                .build();
        
        // Jean a une allergie aux arachides (critique)
        AllergieUtilisateur jeanArachides = AllergieUtilisateur.builder()
                .id(new AllergieUtilisateur.AllergieUtilisateurId(jean.getId(), allergies.get(0).getId()))
                .utilisateur(jean)
                .allergie(allergies.get(0)) // Allergie aux arachides
                .niveauSeverite("CRITIQUE")
                .dateDiagnostic(LocalDateTime.of(2022, 1, 10, 14, 30))
                .diagnostiquePar("Dr. Leroy")
                .notes("Réaction anaphylactique sévère, port d'EpiPen obligatoire")
                .traitementActuel("Éviction totale, auto-injecteur d'épinéphrine")
                .actif(true)
                .build();
        
        allergieUtilisateurRepository.saveAll(Arrays.asList(marieOeufs, jeanArachides));
    }
    
    private void creerReactionsTest(List<Utilisateur> utilisateurs, List<Aliment> aliments) {
        Utilisateur marie = utilisateurs.get(0);
        Utilisateur jean = utilisateurs.get(1);
        
        // Réaction de Marie aux œufs (modérée)
        ReactionAllergique reactionMarieOeufs = ReactionAllergique.builder()
                .utilisateur(marie)
                .aliment(aliments.get(3)) // Œuf de poule
                .dateReaction(LocalDateTime.of(2024, 1, 15, 12, 30))
                .niveauSeverite("MODERE")
                .symptomes("Éruption cutanée, démangeaisons, légers troubles digestifs")
                .dureeReaction(120) // 2 heures
                .quantiteConsommee(BigDecimal.valueOf(50)) // 1/2 œuf
                .traitementUtilise("Antihistaminique oral")
                .contexteConsommation("Œuf dans un gâteau fait maison")
                .lieuReaction("Domicile")
                .medecinConsulte(false)
                .hospitalisationRequise(false)
                .probabiliteAllergie(BigDecimal.valueOf(0.7)) // 70% - allergie confirmée
                .notes("Réaction typique, éviter les œufs à l'avenir")
                .build();
        
        // Réaction de Jean aux cacahuètes (critique)
        ReactionAllergique reactionJeanCacahuetes = ReactionAllergique.builder()
                .utilisateur(jean)
                .aliment(aliments.get(5)) // Cacahuètes
                .dateReaction(LocalDateTime.of(2024, 2, 3, 19, 45))
                .niveauSeverite("CRITIQUE")
                .symptomes("Œdème facial, difficultés respiratoires, chute de tension")
                .dureeReaction(45) // 45 minutes
                .quantiteConsommee(BigDecimal.valueOf(5)) // Quelques grammes
                .traitementUtilise("EpiPen, transport aux urgences")
                .contexteConsommation("Traces dans un plat au restaurant")
                .lieuReaction("Restaurant")
                .medecinConsulte(true)
                .hospitalisationRequise(true)
                .probabiliteAllergie(BigDecimal.valueOf(0.95)) // 95% - réaction anaphylactique
                .notes("Réaction sévère malgré éviction, contamination croisée suspectée")
                .build();
        
        // Réaction test pour déclencher la détection automatique (>30%)
        ReactionAllergique reactionAutoDetection = ReactionAllergique.builder()
                .utilisateur(marie)
                .aliment(aliments.get(4)) // Lait de vache
                .dateReaction(LocalDateTime.now().minusDays(1))
                .niveauSeverite("LEGER")
                .symptomes("Ballonnements, légers crampes abdominales")
                .dureeReaction(60)
                .quantiteConsommee(BigDecimal.valueOf(200)) // 1 verre
                .traitementUtilise("Aucun")
                .contexteConsommation("Lait dans le café du matin")
                .lieuReaction("Domicile")
                .medecinConsulte(false)
                .hospitalisationRequise(false)
                .probabiliteAllergie(BigDecimal.valueOf(0.4)) // 40% - déclenchera la détection auto
                .notes("Première réaction au lait, surveillance nécessaire")
                .build();
        
        reactionAllergiqueRepository.saveAll(Arrays.asList(
            reactionMarieOeufs, reactionJeanCacahuetes, reactionAutoDetection));
        
        log.info("Créé {} réactions de test avec détection automatique", 3);
    }
}
