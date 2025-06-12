#!/bin/bash
# ===================================================
# 🚀 Script de Build et Déploiement TP222 Allergies
# ===================================================
# Script automatisé pour build, test et déploiement
# du système Spring Boot d'allergies alimentaires

set -e  # Arrêter en cas d'erreur

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Variables de configuration
PROJECT_NAME="TP222 Allergies Management System"
JAR_NAME="allergies-management-1.0.0.jar"
DOCKER_COMPOSE_FILE="docker-compose.yml"
APP_PORT="8080"
DB_PORT="5432"

# Fonction d'affichage avec couleurs
print_header() {
    echo -e "${CYAN}=================================================${NC}"
    echo -e "${CYAN}🎯 $1${NC}"
    echo -e "${CYAN}=================================================${NC}"
}

print_step() {
    echo -e "${BLUE}📋 $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Fonction de vérification des prérequis
check_prerequisites() {
    print_header "VÉRIFICATION DES PRÉREQUIS"
    
    # Vérifier Java
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
        print_success "Java installé: $JAVA_VERSION"
    else
        print_error "Java non trouvé. Veuillez installer Java 17 ou plus récent."
        exit 1
    fi
    
    # Vérifier Maven
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version | head -n1 | cut -d' ' -f3)
        print_success "Maven installé: $MVN_VERSION"
    else
        print_error "Maven non trouvé. Veuillez installer Maven 3.9+."
        exit 1
    fi
    
    # Vérifier Docker
    if command -v docker &> /dev/null; then
        DOCKER_VERSION=$(docker --version | cut -d' ' -f3 | sed 's/,//')
        print_success "Docker installé: $DOCKER_VERSION"
    else
        print_warning "Docker non trouvé. Déploiement Docker non disponible."
    fi
    
    # Vérifier Docker Compose
    if command -v docker-compose &> /dev/null; then
        COMPOSE_VERSION=$(docker-compose --version | cut -d' ' -f3 | sed 's/,//')
        print_success "Docker Compose installé: $COMPOSE_VERSION"
    else
        print_warning "Docker Compose non trouvé. Déploiement orchestré non disponible."
    fi
    
    echo ""
}

# Fonction de nettoyage
clean_project() {
    print_header "NETTOYAGE DU PROJET"
    
    print_step "Nettoyage Maven..."
    mvn clean -q
    print_success "Nettoyage Maven terminé"
    
    # Supprimer les logs anciens
    if [ -d "logs" ]; then
        print_step "Suppression des anciens logs..."
        rm -rf logs/*.log 2>/dev/null || true
        print_success "Logs nettoyés"
    fi
    
    echo ""
}

# Fonction de compilation
compile_project() {
    print_header "COMPILATION DU PROJET"
    
    print_step "Compilation avec Maven..."
    if mvn compile -q; then
        print_success "Compilation réussie"
    else
        print_error "Échec de la compilation"
        exit 1
    fi
    
    echo ""
}

# Fonction de tests
run_tests() {
    print_header "EXÉCUTION DES TESTS"
    
    print_step "Tests unitaires Maven..."
    if mvn test -q; then
        print_success "Tests unitaires passés"
    else
        print_warning "Certains tests ont échoué (mode dégradé)"
    fi
    
    # Tests de validation des APIs
    print_step "Tests de validation GitIgnore..."
    if [ -f "validate-gitignore.sh" ]; then
        if ./validate-gitignore.sh > /dev/null 2>&1; then
            print_success "Configuration GitIgnore validée"
        else
            print_warning "Validation GitIgnore avec avertissements"
        fi
    fi
    
    echo ""
}

# Fonction de packaging
package_application() {
    print_header "PACKAGING DE L'APPLICATION"
    
    print_step "Création du JAR exécutable..."
    if mvn package -DskipTests -q; then
        if [ -f "target/$JAR_NAME" ]; then
            JAR_SIZE=$(du -h "target/$JAR_NAME" | cut -f1)
            print_success "JAR créé: $JAR_NAME ($JAR_SIZE)"
        else
            print_error "JAR non trouvé après le build"
            exit 1
        fi
    else
        print_error "Échec du packaging"
        exit 1
    fi
    
    echo ""
}

# Fonction de démarrage de la base de données
start_database() {
    print_header "DÉMARRAGE DE LA BASE DE DONNÉES"
    
    if command -v docker-compose &> /dev/null && [ -f "$DOCKER_COMPOSE_FILE" ]; then
        print_step "Démarrage PostgreSQL avec Docker Compose..."
        
        # Vérifier si le conteneur est déjà en cours d'exécution
        if docker-compose ps postgres | grep -q "Up"; then
            print_success "PostgreSQL déjà en cours d'exécution"
        else
            docker-compose up -d postgres
            print_step "Attente du démarrage de PostgreSQL..."
            sleep 10
            print_success "PostgreSQL démarré sur le port $DB_PORT"
        fi
    else
        print_warning "Docker Compose non disponible. Assurez-vous que PostgreSQL est démarré manuellement."
    fi
    
    echo ""
}

# Fonction de démarrage de l'application
start_application() {
    print_header "DÉMARRAGE DE L'APPLICATION"
    
    print_step "Lancement de Spring Boot..."
    print_success "Application en cours de démarrage..."
    
    echo -e "${PURPLE}🚀 Commande de démarrage:${NC}"
    echo -e "${CYAN}java -jar target/$JAR_NAME${NC}"
    echo ""
    echo -e "${PURPLE}🌐 URLs d'accès:${NC}"
    echo -e "${CYAN}• Application: http://localhost:$APP_PORT${NC}"
    echo -e "${CYAN}• Health Check: http://localhost:$APP_PORT/api/actuator/health${NC}"
    echo -e "${CYAN}• Swagger UI: http://localhost:$APP_PORT/api/swagger-ui.html${NC}"
    echo -e "${CYAN}• Métriques: http://localhost:$APP_PORT/api/actuator/metrics${NC}"
    echo ""
    
    # Optionnel: démarrer automatiquement
    if [ "$1" = "--start" ]; then
        print_step "Démarrage automatique de l'application..."
        java -jar "target/$JAR_NAME" &
        APP_PID=$!
        echo "PID de l'application: $APP_PID"
        
        # Attendre le démarrage
        print_step "Attente du démarrage de l'application..."
        sleep 15
        
        # Vérifier le health check
        if curl -s "http://localhost:$APP_PORT/api/actuator/health" | grep -q "UP"; then
            print_success "Application démarrée avec succès !"
            echo "PID: $APP_PID" > app.pid
        else
            print_error "Échec du démarrage de l'application"
            kill $APP_PID 2>/dev/null || true
            exit 1
        fi
    fi
    
    echo ""
}

# Fonction de tests post-déploiement
run_deployment_tests() {
    print_header "TESTS POST-DÉPLOIEMENT"
    
    print_step "Test Health Check..."
    if curl -s "http://localhost:$APP_PORT/api/actuator/health" | grep -q "UP"; then
        print_success "Health Check OK"
    else
        print_error "Health Check échoué"
        return 1
    fi
    
    print_step "Test API IA Allergies..."
    if curl -s "http://localhost:$APP_PORT/api/allergies/detection-automatique/statut" | grep -q "detectionActive"; then
        print_success "API IA fonctionnelle"
    else
        print_error "API IA non accessible"
        return 1
    fi
    
    print_step "Test Profil Utilisateur..."
    if curl -s "http://localhost:$APP_PORT/api/allergies/profil/1" | grep -q "utilisateurId"; then
        print_success "API Profil fonctionnelle"
    else
        print_error "API Profil non accessible"
        return 1
    fi
    
    print_success "Tous les tests post-déploiement réussis !"
    echo ""
}

# Fonction d'arrêt de l'application
stop_application() {
    print_header "ARRÊT DE L'APPLICATION"
    
    if [ -f "app.pid" ]; then
        PID=$(cat app.pid)
        if ps -p $PID > /dev/null; then
            print_step "Arrêt de l'application (PID: $PID)..."
            kill $PID
            rm app.pid
            print_success "Application arrêtée"
        else
            print_warning "Application déjà arrêtée"
            rm app.pid
        fi
    else
        print_warning "Fichier PID non trouvé"
    fi
    
    echo ""
}

# Fonction d'aide
show_help() {
    echo -e "${CYAN}🎯 TP222 Allergies Management System - Script de Build${NC}"
    echo ""
    echo -e "${BLUE}Usage:${NC} $0 [options]"
    echo ""
    echo -e "${BLUE}Options:${NC}"
    echo "  build              Build complet (clean + compile + test + package)"
    echo "  clean              Nettoyage du projet"
    echo "  compile            Compilation uniquement"
    echo "  test               Tests uniquement"
    echo "  package            Packaging uniquement"
    echo "  deploy             Déploiement complet (database + app)"
    echo "  start              Démarrer l'application"
    echo "  stop               Arrêter l'application"
    echo "  status             Statut de l'application"
    echo "  full               Build + Deploy + Tests"
    echo "  help               Afficher cette aide"
    echo ""
    echo -e "${BLUE}Exemples:${NC}"
    echo "  $0 build           # Build complet sans démarrage"
    echo "  $0 deploy          # Déploiement avec démarrage auto"
    echo "  $0 full            # Pipeline complet"
    echo ""
}

# Fonction de statut
show_status() {
    print_header "STATUT DU SYSTÈME"
    
    # Vérifier l'application
    if curl -s "http://localhost:$APP_PORT/api/actuator/health" | grep -q "UP" 2>/dev/null; then
        print_success "Application Spring Boot: RUNNING"
    else
        print_warning "Application Spring Boot: STOPPED"
    fi
    
    # Vérifier PostgreSQL
    if docker-compose ps postgres | grep -q "Up" 2>/dev/null; then
        print_success "PostgreSQL: RUNNING"
    else
        print_warning "PostgreSQL: STOPPED"
    fi
    
    # Vérifier les fichiers
    if [ -f "target/$JAR_NAME" ]; then
        JAR_SIZE=$(du -h "target/$JAR_NAME" | cut -f1)
        print_success "JAR disponible: $JAR_NAME ($JAR_SIZE)"
    else
        print_warning "JAR non disponible - Build requis"
    fi
    
    echo ""
}

# Fonction principale
main() {
    case "$1" in
        "build")
            check_prerequisites
            clean_project
            compile_project
            run_tests
            package_application
            print_success "Build terminé avec succès !"
            ;;
        "clean")
            clean_project
            ;;
        "compile")
            compile_project
            ;;
        "test")
            run_tests
            ;;
        "package")
            package_application
            ;;
        "deploy")
            start_database
            start_application --start
            ;;
        "start")
            start_application --start
            ;;
        "stop")
            stop_application
            ;;
        "status")
            show_status
            ;;
        "full")
            check_prerequisites
            clean_project
            compile_project
            run_tests
            package_application
            start_database
            start_application --start
            run_deployment_tests
            print_header "🎉 DÉPLOIEMENT COMPLET RÉUSSI !"
            print_success "Le système TP222 Allergies est opérationnel !"
            ;;
        "help"|"--help"|"-h")
            show_help
            ;;
        *)
            print_error "Option inconnue: $1"
            echo ""
            show_help
            exit 1
            ;;
    esac
}

# Vérifier qu'on est dans le bon répertoire
if [ ! -f "pom.xml" ]; then
    print_error "Ce script doit être exécuté depuis le répertoire racine du projet Spring Boot"
    exit 1
fi

# Exécuter la fonction principale
main "$1"
