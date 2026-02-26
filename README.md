# Projet MasterAnnonce - Migration Spring Boot (TP AIR #4)

Ce dépôt contient le code pour le projet **MasterAnnonce** refondu sous une architecture **Spring Boot 3**.

## Architecture & Choix Techniques

- **Framework** : Spring Boot (Web, Data JPA, Security)
- **Base de données** : PostgreSQL en production / développement local. Pour les tests d'intégration, nous utilisons **Testcontainers** car il permet d'instancier une base PostgreSQL éphémère isolée et 100% semblable à la production.
- **Architecture en couches** :
  - `controllers` (@RestController) pour exposer les endpoints.
  - `services` (@Service) pour encapsuler la logique métier et la sécurité method-level.
  - `repositories` (@Repository) utilisant `JpaRepository` et `JpaSpecificationExecutor`.
- **Sécurité & JWT** : Spring Security a été configuré en mode stateless avec un token JWT et une signature sécurisée (HMAC+SHA256). Les vérifications de rôles sont implémentées via `@PreAuthorize`.
- **Logging AOP** : Implémentation via l'aspect `@Around` avec un Filter qui injecte un paramètre `correlationId` (MDC) pour chaque requête HTTP.
- **MapStruct** : Implémenté pour convertir les entités en DTO.

## Choix pour GitHub Actions CI: Testcontainers

Dans le cadre de la CI, j'ai choisi la stratégie **Testcontainers (Option 1)** plutôt que l'utilisation de `services` GitHub Actions.
**Raisons :**

1. Une configuration unifiée entre l'environnement de développement local et la CI.
2. Les conteneurs démarrent via un cycle de vie contrôlé directement par le code Java (JUnit 5 + `Testcontainers`), ce qui évite les faux positifs dus à l'instabilité de _liveness probe_ dans le workflow Yaml.
3. C'est le standard industriel le plus robuste lors des tests d'intégration avec Spring Boot.

L'artefact compilé final s'appelle `master-annonce-jar` (généré au chemin `target/TP_AIR-1.0-SNAPSHOT.jar`).

## Problèmes rencontrés & Solutions

- **Migration MapStruct / Entities Lazys** : Le mappage des entités aux DTO risquait de remonter des propriétés non initialisées (Lazy Loading). Les "ignores" stricts ont été appliquées pour ne mapper que les champs explicites requis tels que `authorId`.
- **Version de Java & Mockito Agent** : Les nouvelles versions JDK >= 21 et Mockito requièrent l'activation de `EnableDynamicAgentLoading` (ajouter le flag au surfire plugin).

## Lancement Rapide (Docker Multi-Stage)

Pour construire le projet complet (Backend Java 21 + Base de données PostgreSQL) et initialiser les données via le `DatabaseSeeder`, exécutez la commande suivante à la racine :

```powershell
docker compose up -d --build
```

L'API REST sera disponible sur : `http://localhost:8080/api/annonces`  
Swagger Documentation (OpenAPI v3) : `http://localhost:8080/swagger-ui/index.html`  
Actuator Health : `http://localhost:8080/actuator/health`

## Nouveautés et Résolution de Problèmes (TP AIR 04)

Durant cette migration Spring Boot, les points suivants ont été adressés et corrigés :

1. **Implémentation CRUD (Category & User)** : Remplacement des DAO JAX-RS par des interfaces _Spring Data JPA_ (`JpaRepository`).
2. **Gestion des erreurs MapStruct (Contrainte Not-Null `category_id`)** : L'injection de dépendances a été modifiée pour permettre à `AnnonceService` de retrouver dynamiquement les entités `Category` depuis leur label en base de données, empêchant les `NullPointerException` durant l'insertion.
3. **Tests de charge (`load-test.ps1` & `load-test.sh`)** : Correction des payloads JSON pour utiliser une catégorie valide (ex. `"Electronique"`). **Résultat : 50/50 requêtes réussies.**
4. **Correction Swagger UI (Erreur 500)** : Le passage à Spring Boot 3.4+ entraînait un plantage du Swagger UI (`NoSuchMethodError` dans `ControllerAdviceBean`). La version de **springdoc-openapi-starter-webmvc-ui** a été mise à jour vers `2.8.4` pour restaurer la compatibilité.
5. **Génération de Données (DatabaseSeeder)** : Un chargeur de données est automatiquement exécuté au démarrage (`CommandLineRunner`) pour peupler PostgreSQL avec des utilisateurs (Administrateurs & Users), catégories et annonces factices.
6. **Sécurité JWT** : L'ancienne configuration JAAS a été entièrement migrée vers **Spring Security** avec configuration _stateless_ et intégration des filtres de tokens (`OncePerRequestFilter`).
