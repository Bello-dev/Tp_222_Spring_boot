# Dockerfile pour Spring Boot Allergies Management System
# Équivalent du Dockerfile Flask mais pour Java/Spring Boot

# Étape 1: Build de l'application avec Maven
FROM maven:3.9.4-openjdk-21-slim AS build

WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY src src

# Build de l'application (skip tests pour accélérer le build)
RUN mvn clean package -DskipTests

# Étape 2: Image de production légère
FROM openjdk:21-jdk-slim

# Métadonnées
LABEL maintainer="TP222 Allergies Team"
LABEL description="Spring Boot Allergies Management System with AI Detection"
LABEL version="1.0.0"

# Variables d'environnement
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8080
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Créer un utilisateur non-root pour la sécurité
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Répertoire de travail
WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Changer la propriété du fichier
RUN chown spring:spring app.jar

# Basculer vers l'utilisateur non-root
USER spring:spring

# Port exposé
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
