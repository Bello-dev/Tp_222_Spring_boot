package com.tp222.allergies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Classe principale de l'application Spring Boot pour la gestion des allergies
 */
@SpringBootApplication
@EnableJpaAuditing
public class AllergiesApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllergiesApplication.class, args);
    }
}
