#!/bin/bash
# =============================================
# 🔍 Script de Validation GitIgnore TP222
# =============================================
# Teste que les fichiers importants sont bien ignorés

echo "🔍 VALIDATION CONFIGURATION GITIGNORE"
echo "====================================="

cd "$(dirname "$0")"

# Couleurs pour l'affichage
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Fonction de test
test_ignored() {
    local file=$1
    local description=$2
    
    # Créer le fichier temporairement
    echo "test" > "$file"
    
    # Vérifier s'il est ignoré
    if git check-ignore "$file" > /dev/null 2>&1; then
        echo -e "✅ ${GREEN}IGNORÉ${NC}: $description ($file)"
        result=0
    else
        echo -e "❌ ${RED}NON IGNORÉ${NC}: $description ($file)"
        result=1
    fi
    
    # Supprimer le fichier de test
    rm -f "$file"
    return $result
}

echo ""
echo "📁 Tests des dossiers et fichiers Maven/Spring Boot:"
echo "------------------------------------------------"
test_ignored "target/test.txt" "Dossier target Maven"
test_ignored "application.log" "Logs Spring Boot"
test_ignored "spring.log" "Logs Spring spécifiques"

echo ""
echo "🔒 Tests des fichiers de configuration sensibles:"
echo "----------------------------------------------"
test_ignored "application-secrets.properties" "Configuration secrets"
test_ignored "application-local.properties" "Configuration locale"
test_ignored ".env" "Variables d'environnement"

echo ""
echo "💻 Tests des fichiers IDEs:"
echo "--------------------------"
mkdir -p .idea && touch .idea/test.xml
if git check-ignore .idea/test.xml > /dev/null 2>&1; then
    echo -e "✅ ${GREEN}IGNORÉ${NC}: IntelliJ IDEA (.idea/)"
else
    echo -e "❌ ${RED}NON IGNORÉ${NC}: IntelliJ IDEA (.idea/)"
fi
rm -rf .idea

test_ignored "project.iml" "Fichiers IntelliJ .iml"
test_ignored ".vscode/settings.json" "Configuration VS Code"

echo ""
echo "🐳 Tests des fichiers Docker:"
echo "---------------------------"
test_ignored "docker-compose.override.yml" "Override Docker Compose"
mkdir -p docker-data && touch docker-data/test.txt
if git check-ignore docker-data/test.txt > /dev/null 2>&1; then
    echo -e "✅ ${GREEN}IGNORÉ${NC}: Données Docker (docker-data/)"
else
    echo -e "❌ ${RED}NON IGNORÉ${NC}: Données Docker (docker-data/)"
fi
rm -rf docker-data

echo ""
echo "🗄️ Tests des fichiers de base de données:"
echo "---------------------------------------"
test_ignored "database.h2.db" "Base H2"
test_ignored "backup.sql" "Dump SQL"

echo ""
echo "🔐 Tests des fichiers de sécurité:"
echo "--------------------------------"
test_ignored "keystore.jks" "Keystore Java"
test_ignored "cert.pem" "Certificat SSL"
test_ignored "api-keys.txt" "Clés API"

echo ""
echo "📊 Tests spécifiques TP222 Allergies:"
echo "-----------------------------------"
mkdir -p reports/allergy-reports && touch reports/allergy-reports/test.pdf
if git check-ignore reports/allergy-reports/test.pdf > /dev/null 2>&1; then
    echo -e "✅ ${GREEN}IGNORÉ${NC}: Rapports allergies (reports/)"
else
    echo -e "❌ ${RED}NON IGNORÉ${NC}: Rapports allergies (reports/)"
fi
rm -rf reports

test_ignored "allergies-management-snapshot.jar" "JAR Snapshot"
test_ignored "application-staging.yml" "Config staging"

echo ""
echo "🎯 RÉSUMÉ DE LA VALIDATION"
echo "========================="

# Compter les fichiers actuellement trackés
tracked_files=$(git ls-files | wc -l)
echo "📁 Fichiers actuellement suivis par Git: $tracked_files"

# Vérifier les fichiers importants qui DOIVENT être suivis
important_files=("README.md" "pom.xml" "src/main/java" "src/main/resources/application.yml" ".gitignore")
missing_files=()

for file in "${important_files[@]}"; do
    if [[ -e "$file" ]]; then
        echo -e "✅ ${GREEN}PRÉSENT${NC}: $file"
    else
        missing_files+=("$file")
        echo -e "⚠️ ${YELLOW}MANQUANT${NC}: $file"
    fi
done

echo ""
if [[ ${#missing_files[@]} -eq 0 ]]; then
    echo -e "🎉 ${GREEN}CONFIGURATION GITIGNORE PARFAITE !${NC}"
    echo "✅ Tous les fichiers sensibles sont ignorés"
    echo "✅ Tous les fichiers importants sont présents"
else
    echo -e "⚠️ ${YELLOW}CONFIGURATION CORRECTE AVEC AVERTISSEMENTS${NC}"
    echo "⚠️ Fichiers manquants: ${missing_files[*]}"
fi

echo ""
echo "📖 Pour plus d'informations, consultez le fichier .gitignore"
echo "🔧 Pour modifier les règles, éditez .gitignore selon vos besoins"
