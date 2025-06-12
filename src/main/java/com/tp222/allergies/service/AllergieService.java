package com.tp222.allergies.service;

import com.tp222.allergies.model.entity.*;
import com.tp222.allergies.model.dto.request.ReactionAllergiqueRequest;
import com.tp222.allergies.model.dto.response.ProfilAllergiqueResponse;
import com.tp222.allergies.model.dto.response.VerificationRisqueResponse;
import com.tp222.allergies.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des allergies avec fonctionnalités d'IA
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AllergieService {
    
    private final AllergieRepository allergieRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AlimentRepository alimentRepository;
    private final AllergieUtilisateurRepository allergieUtilisateurRepository;
    private final ReactionAllergiqueRepository reactionAllergiqueRepository;
    
    // Seuil de détection automatique d'allergie (>30%)
    private static final BigDecimal SEUIL_DETECTION_ALLERGIE = BigDecimal.valueOf(0.3);
    
    /**
     * Enregistre une nouvelle réaction allergique avec analyse IA
     */
    public ReactionAllergique enregistrerReaction(ReactionAllergiqueRequest request) {
        log.info("Enregistrement d'une nouvelle réaction allergique pour utilisateur: {} et aliment: {}", 
                request.getUtilisateurId(), request.getAlimentId());
        
        Utilisateur utilisateur = utilisateurRepository.findById(request.getUtilisateurId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Aliment aliment = alimentRepository.findById(request.getAlimentId())
                .orElseThrow(() -> new RuntimeException("Aliment non trouvé"));
        
        // Création de la réaction
        ReactionAllergique reaction = ReactionAllergique.builder()
                .utilisateur(utilisateur)
                .aliment(aliment)
                .dateReaction(request.getDateReaction())
                .niveauSeverite(request.getNiveauSeverite())
                .symptomes(request.getSymptomes())
                .dureeReaction(request.getDureeReaction())
                .quantiteConsommee(request.getQuantiteConsommee())
                .traitementUtilise(request.getTraitementUtilise())
                .contexteConsommation(request.getContexteConsommation())
                .lieuReaction(request.getLieuReaction())
                .medecinConsulte(request.getMedecinConsulte())
                .hospitalisationRequise(request.getHospitalisationRequise())
                .probabiliteAllergie(calculerProbabiliteAllergie(request, utilisateur, aliment))
                .notes(request.getNotes())
                .build();
        
        reaction = reactionAllergiqueRepository.save(reaction);
        
        // Détection automatique d'allergie si probabilité > 30%
        if (reaction.isAllergic()) {
            detecterEtCreerAllergie(utilisateur, aliment, reaction);
        }
        
        log.info("Réaction allergique enregistrée avec ID: {} et probabilité: {}", 
                reaction.getId(), reaction.getProbabiliteAllergie());
        
        return reaction;
    }
    
    /**
     * Vérifie le risque d'allergie pour un utilisateur et un aliment
     */
    public VerificationRisqueResponse verifierRisque(Long utilisateurId, Long alimentId) {
        log.info("Vérification du risque d'allergie pour utilisateur: {} et aliment: {}", utilisateurId, alimentId);
        
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Aliment aliment = alimentRepository.findById(alimentId)
                .orElseThrow(() -> new RuntimeException("Aliment non trouvé"));
        
        // Récupération des allergies connues
        List<AllergieUtilisateur> allergiesUtilisateur = allergieUtilisateurRepository
                .findByUtilisateurAndActifTrue(utilisateur);
        
        // Récupération des réactions passées
        List<ReactionAllergique> reactionsPassees = reactionAllergiqueRepository
                .findByUtilisateurAndAliment(utilisateur, aliment);
        
        // Analyse du risque
        VerificationRisqueResponse.AnalyseIA analyseIA = analyserRisqueIA(
                utilisateur, aliment, allergiesUtilisateur, reactionsPassees);
        
        int scoreRisque = calculerScoreRisque(allergiesUtilisateur, reactionsPassees, aliment);
        String niveauRisque = determinerNiveauRisque(scoreRisque);
        
        return VerificationRisqueResponse.builder()
                .risqueDetecte(scoreRisque > 30)
                .niveauRisque(niveauRisque)
                .scoreRisque(scoreRisque)
                .utilisateurId(utilisateurId)
                .alimentId(alimentId)
                .nomAliment(aliment.getNom())
                .allergiesDetectees(mapAllergiesRisque(allergiesUtilisateur, aliment))
                .reactionsPassees(mapReactionsHistoriques(reactionsPassees))
                .recommandations(genererRecommandations(scoreRisque, allergiesUtilisateur))
                .analyseIA(analyseIA)
                .build();
    }
    
    /**
     * Obtient le profil allergique complet d'un utilisateur
     */
    public ProfilAllergiqueResponse getProfilAllergique(Long utilisateurId) {
        log.info("Récupération du profil allergique pour utilisateur: {}", utilisateurId);
        
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        List<AllergieUtilisateur> allergies = allergieUtilisateurRepository
                .findByUtilisateurAndActifTrue(utilisateur);
        
        List<ReactionAllergique> reactionsRecentes = reactionAllergiqueRepository
                .findRecentReactionsByUser(utilisateur, LocalDateTime.now().minusMonths(3));
        
        ProfilAllergiqueResponse.StatistiquesAllergie stats = calculerStatistiques(utilisateur);
        
        return ProfilAllergiqueResponse.builder()
                .utilisateurId(utilisateur.getId())
                .nomUtilisateur(utilisateur.getNom())
                .prenomUtilisateur(utilisateur.getPrenom())
                .emailUtilisateur(utilisateur.getEmail())
                .allergies(mapAllergiesInfo(allergies))
                .reactionsRecentes(mapReactionsRecentesInfo(reactionsRecentes))
                .statistiques(stats)
                .build();
    }
    
    /**
     * Calcule la probabilité d'allergie basée sur plusieurs facteurs
     */
    private BigDecimal calculerProbabiliteAllergie(ReactionAllergiqueRequest request, 
                                                  Utilisateur utilisateur, Aliment aliment) {
        BigDecimal probabilite = BigDecimal.ZERO;
        
        // Facteur 1: Sévérité de la réaction (0.4 max)
        probabilite = probabilite.add(getProbabiliteBySeverite(request.getNiveauSeverite()));
        
        // Facteur 2: Intervention médicale (0.3 max)
        if (Boolean.TRUE.equals(request.getHospitalisationRequise())) {
            probabilite = probabilite.add(BigDecimal.valueOf(0.3));
        } else if (Boolean.TRUE.equals(request.getMedecinConsulte())) {
            probabilite = probabilite.add(BigDecimal.valueOf(0.15));
        }
        
        // Facteur 3: Historique avec cet aliment (0.2 max)
        List<ReactionAllergique> historique = reactionAllergiqueRepository
                .findByUtilisateurAndAliment(utilisateur, aliment);
        if (!historique.isEmpty()) {
            probabilite = probabilite.add(BigDecimal.valueOf(0.1 * Math.min(historique.size(), 2)));
        }
        
        // Facteur 4: Allergènes connus dans l'aliment (0.1 max)
        if (aliment.containsAllergenes()) {
            probabilite = probabilite.add(BigDecimal.valueOf(0.1));
        }
        
        return probabilite.min(BigDecimal.ONE); // Max 1.0
    }
    
    private BigDecimal getProbabiliteBySeverite(String severite) {
        if (severite == null) return BigDecimal.ZERO;
        return switch (severite) {
            case "CRITIQUE" -> BigDecimal.valueOf(0.4);
            case "SEVERE" -> BigDecimal.valueOf(0.3);
            case "MODERE" -> BigDecimal.valueOf(0.2);
            case "LEGER" -> BigDecimal.valueOf(0.1);
            default -> BigDecimal.ZERO;
        };
    }
    
    /**
     * Détecte automatiquement une allergie et la crée si nécessaire
     */
    private void detecterEtCreerAllergie(Utilisateur utilisateur, Aliment aliment, ReactionAllergique reaction) {
        log.info("Détection automatique d'allergie potentielle pour utilisateur: {} et aliment: {}", 
                utilisateur.getId(), aliment.getId());
        
        // Vérifier si l'utilisateur a déjà cette allergie alimentaire
        List<AllergieUtilisateur> allergiesExistantes = allergieUtilisateurRepository
                .findByUtilisateurAndActifTrue(utilisateur);
        
        boolean allergieDejaConnue = allergiesExistantes.stream()
                .anyMatch(au -> au.getAllergie().isAlimentaire() && 
                         au.getAllergie().getNom().toLowerCase().contains(aliment.getNom().toLowerCase()));
        
        if (!allergieDejaConnue) {
            // Créer une nouvelle allergie automatiquement détectée
            Allergie nouvelleAllergie = creerAllergieAutomatique(aliment, reaction);
            associerAllergieUtilisateur(utilisateur, nouvelleAllergie, reaction);
        }
    }
    
    private Allergie creerAllergieAutomatique(Aliment aliment, ReactionAllergique reaction) {
        return Allergie.builder()
                .nom("Allergie à " + aliment.getNom())
                .description("Allergie détectée automatiquement par le système IA")
                .niveauSeverite(reaction.getNiveauSeverite())
                .typeAllergie("ALIMENTAIRE")
                .symptomes(reaction.getSymptomes())
                .traitementRecommande("Consultation médicale recommandée pour confirmation")
                .actif(true)
                .build();
    }
    
    private void associerAllergieUtilisateur(Utilisateur utilisateur, Allergie allergie, ReactionAllergique reaction) {
        AllergieUtilisateur.AllergieUtilisateurId id = new AllergieUtilisateur.AllergieUtilisateurId(
                utilisateur.getId(), allergie.getId());
        
        AllergieUtilisateur association = AllergieUtilisateur.builder()
                .id(id)
                .utilisateur(utilisateur)
                .allergie(allergie)
                .niveauSeverite(reaction.getNiveauSeverite())
                .dateDiagnostic(LocalDateTime.now())
                .diagnostiquePar("Système IA - Détection automatique")
                .notes("Allergie détectée automatiquement suite à une réaction avec probabilité > 30%")
                .actif(true)
                .build();
        
        allergieUtilisateurRepository.save(association);
    }
    
    // Méthodes de mapping et d'analyse privées (suite dans le prochain message)
    
    private VerificationRisqueResponse.AnalyseIA analyserRisqueIA(Utilisateur utilisateur, Aliment aliment, 
                                                                 List<AllergieUtilisateur> allergies, 
                                                                 List<ReactionAllergique> reactions) {
        BigDecimal probabiliteGlobale = BigDecimal.ZERO;
        List<String> facteursRisque = new ArrayList<>();
        List<String> mesuresPrevention = new ArrayList<>();
        StringBuilder raisonnement = new StringBuilder();
        
        // Analyse des allergies existantes
        for (AllergieUtilisateur allergie : allergies) {
            if (allergie.getAllergie().isAlimentaire()) {
                probabiliteGlobale = probabiliteGlobale.add(BigDecimal.valueOf(0.2));
                facteursRisque.add("Allergie alimentaire connue: " + allergie.getAllergie().getNom());
            }
        }
        
        // Analyse des réactions passées
        if (!reactions.isEmpty()) {
            BigDecimal moyenneProbabilite = reactions.stream()
                    .filter(r -> r.getProbabiliteAllergie() != null)
                    .map(ReactionAllergique::getProbabiliteAllergie)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(reactions.size()), 2, java.math.RoundingMode.HALF_UP);
            
            probabiliteGlobale = probabiliteGlobale.add(moyenneProbabilite);
            facteursRisque.add("Historique de " + reactions.size() + " réaction(s) à cet aliment");
        }
        
        // Analyse de l'aliment
        if (aliment.containsAllergenes()) {
            probabiliteGlobale = probabiliteGlobale.add(BigDecimal.valueOf(0.1));
            facteursRisque.add("Aliment contient des allergènes potentiels");
        }
        
        // Génération des mesures de prévention
        if (probabiliteGlobale.compareTo(SEUIL_DETECTION_ALLERGIE) > 0) {
            mesuresPrevention.add("Éviter la consommation de " + aliment.getNom());
            mesuresPrevention.add("Consulter un allergologue");
            mesuresPrevention.add("Porter un auto-injecteur d'épinéphrine si prescrit");
        }
        
        raisonnement.append("Analyse basée sur le seuil de détection de 30%. ");
        raisonnement.append("Probabilité calculée: ").append(probabiliteGlobale.multiply(BigDecimal.valueOf(100)));
        raisonnement.append("%. Facteurs considérés: allergies connues, historique des réactions, composition de l'aliment.");
        
        return VerificationRisqueResponse.AnalyseIA.builder()
                .probabiliteAllergieGlobale(probabiliteGlobale)
                .raisonnement(raisonnement.toString())
                .facteursRisque(facteursRisque)
                .mesuresPrevention(mesuresPrevention)
                .consultationMedicaleRecommandee(probabiliteGlobale.compareTo(BigDecimal.valueOf(0.5)) > 0)
                .seuillDetection(">30% de probabilité")
                .build();
    }
    
    private int calculerScoreRisque(List<AllergieUtilisateur> allergies, 
                                   List<ReactionAllergique> reactions, Aliment aliment) {
        int score = 0;
        
        // Score basé sur les allergies existantes
        for (AllergieUtilisateur allergie : allergies) {
            if (allergie.isCritique()) score += 30;
            else score += 15;
        }
        
        // Score basé sur les réactions passées
        for (ReactionAllergique reaction : reactions) {
            score += reaction.calculateRiskScore() / 4; // Pondération
        }
        
        // Score basé sur l'aliment
        if (aliment.containsAllergenes()) score += 10;
        
        return Math.min(score, 100);
    }
    
    private String determinerNiveauRisque(int score) {
        if (score >= 70) return "CRITIQUE";
        if (score >= 50) return "ELEVE";
        if (score >= 30) return "MODERE";
        return "FAIBLE";
    }
    
    private List<String> genererRecommandations(int scoreRisque, List<AllergieUtilisateur> allergies) {
        List<String> recommandations = new ArrayList<>();
        
        if (scoreRisque >= 70) {
            recommandations.add("URGENT: Éviter absolument cet aliment");
            recommandations.add("Consulter immédiatement un allergologue");
            recommandations.add("Porter en permanence un auto-injecteur d'épinéphrine");
        } else if (scoreRisque >= 50) {
            recommandations.add("Éviter cet aliment");
            recommandations.add("Consulter un allergologue dans les plus brefs délais");
            recommandations.add("Avoir des antihistaminiques à portée de main");
        } else if (scoreRisque >= 30) {
            recommandations.add("Consommer avec prudence et en petites quantités");
            recommandations.add("Surveiller l'apparition de symptômes");
            recommandations.add("Consulter un médecin pour évaluation");
        } else {
            recommandations.add("Risque faible - consommation possible avec surveillance");
            recommandations.add("Rester attentif aux éventuels symptômes");
        }
        
        return recommandations;
    }
    
    // Méthodes de mapping des DTOs
    
    private List<ProfilAllergiqueResponse.AllergieInfo> mapAllergiesInfo(List<AllergieUtilisateur> allergies) {
        return allergies.stream()
                .map(au -> ProfilAllergiqueResponse.AllergieInfo.builder()
                        .allergieId(au.getAllergie().getId())
                        .nomAllergie(au.getAllergie().getNom())
                        .typeAllergie(au.getAllergie().getTypeAllergie())
                        .niveauSeverite(au.getNiveauSeverite())
                        .dateDiagnostic(au.getDateDiagnostic())
                        .diagnostiquePar(au.getDiagnostiquePar())
                        .notes(au.getNotes())
                        .actif(au.getActif())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<ProfilAllergiqueResponse.ReactionRecenteInfo> mapReactionsRecentesInfo(List<ReactionAllergique> reactions) {
        return reactions.stream()
                .map(r -> ProfilAllergiqueResponse.ReactionRecenteInfo.builder()
                        .reactionId(r.getId())
                        .nomAliment(r.getAliment().getNom())
                        .dateReaction(r.getDateReaction())
                        .niveauSeverite(r.getNiveauSeverite())
                        .symptomes(r.getSymptomes())
                        .hospitalisationRequise(r.getHospitalisationRequise())
                        .probabiliteAllergie(r.getProbabiliteAllergie())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<VerificationRisqueResponse.AllergieRisque> mapAllergiesRisque(List<AllergieUtilisateur> allergies, Aliment aliment) {
        return allergies.stream()
                .filter(au -> au.getAllergie().isAlimentaire())
                .map(au -> VerificationRisqueResponse.AllergieRisque.builder()
                        .allergieId(au.getAllergie().getId())
                        .nomAllergie(au.getAllergie().getNom())
                        .niveauSeverite(au.getNiveauSeverite())
                        .probabiliteReaction(BigDecimal.valueOf(0.5)) // Estimation
                        .typeAllergie(au.getAllergie().getTypeAllergie())
                        .critique(au.isCritique())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<VerificationRisqueResponse.ReactionHistorique> mapReactionsHistoriques(List<ReactionAllergique> reactions) {
        return reactions.stream()
                .map(r -> VerificationRisqueResponse.ReactionHistorique.builder()
                        .dateReaction(r.getDateReaction())
                        .niveauSeverite(r.getNiveauSeverite())
                        .symptomes(r.getSymptomes())
                        .probabiliteAllergie(r.getProbabiliteAllergie())
                        .hospitalisationRequise(r.getHospitalisationRequise())
                        .build())
                .collect(Collectors.toList());
    }
    
    private ProfilAllergiqueResponse.StatistiquesAllergie calculerStatistiques(Utilisateur utilisateur) {
        List<AllergieUtilisateur> allergies = allergieUtilisateurRepository.findByUtilisateurAndActifTrue(utilisateur);
        List<ReactionAllergique> toutesReactions = reactionAllergiqueRepository.findByUtilisateur(utilisateur);
        List<ReactionAllergique> reactionsMois = reactionAllergiqueRepository
                .findRecentReactionsByUser(utilisateur, LocalDateTime.now().minusMonths(1));
        
        int nombreAllergiesCritiques = (int) allergies.stream()
                .filter(AllergieUtilisateur::isCritique)
                .count();
        
        // Calcul du score de risque global
        int scoreRisqueGlobal = allergies.stream()
                .mapToInt(au -> au.isCritique() ? 30 : 15)
                .sum();
        
        return ProfilAllergiqueResponse.StatistiquesAllergie.builder()
                .nombreAllergies(allergies.size())
                .nombreAllergiesCritiques(nombreAllergiesCritiques)
                .nombreReactionsTotal(toutesReactions.size())
                .nombreReactionsDernierMois(reactionsMois.size())
                .allergieRecurrente(trouverAllergieRecurrente(allergies))
                .alimentRisque(trouverAlimentRisque(toutesReactions))
                .scoreRisqueGlobal(Math.min(scoreRisqueGlobal, 100))
                .build();
    }
    
    private String trouverAllergieRecurrente(List<AllergieUtilisateur> allergies) {
        return allergies.stream()
                .filter(au -> au.getAllergie().isAlimentaire())
                .findFirst()
                .map(au -> au.getAllergie().getNom())
                .orElse("Aucune");
    }
    
    private String trouverAlimentRisque(List<ReactionAllergique> reactions) {
        return reactions.stream()
                .filter(r -> r.isAllergic())
                .findFirst()
                .map(r -> r.getAliment().getNom())
                .orElse("Aucun");
    }
}
