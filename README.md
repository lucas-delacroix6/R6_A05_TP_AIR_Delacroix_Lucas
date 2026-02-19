# MasterAnnonce – Backend API REST

> TP Dev Avancé #3 – IUT Montreuil BUT 3  
> Backend Java professionnel : JAX-RS, JPA/Hibernate, JAAS, tests, OpenAPI

---

## Architecture

```
Client (Postman / Front JS)
        ↓ HTTP / JSON
API REST (JAX-RS – Jersey)          ← com.example.tp_air.api.*
        ↓
Service (transactions + règles métier) ← com.example.tp_air.services*
        ↓
Repository (JPA / Hibernate)        ← com.example.tp_air.repositories*
        ↓
PostgreSQL (prod) / H2 in-memory (tests)
```

### Packages

| Package      | Rôle                                                              |
| ------------ | ----------------------------------------------------------------- |
| `api`        | Ressources JAX-RS, mappers d'exceptions                           |
| `dto`        | Data Transfer Objects (entrée/sortie JSON) avec Bean Validation   |
| `models`     | Entités JPA (`Annonce`, `User`) avec `@Version`                   |
| `repositories` | Accès base de données (JPA/Hibernate)                             |
| `services`    | Logique métier et transactions                                    |
| `security`   | JAAS (DbLoginModule, TokenLoginModule), TokenStore, filtre JAX-RS |
| `exceptions`  | Exceptions métier custom                                          |

---

## Prérequis

- Java 11+
- Maven 3.8+
- PostgreSQL 13+ (pour la prod)
- Serveur d'application compatible Jakarta EE (Tomcat 10+)

---

## Configuration

### Base de données (PostgreSQL)

```bash
createdb masterannonce
psql masterannonce < src/main/resources/init.sql
```

Modifier les credentials dans `src/main/resources/META-INF/persistence.xml` si besoin.

### JAAS

Ajouter l'option JVM au démarrage de Tomcat :

```
-Djava.security.auth.login.config=/chemin/vers/masterannonce/src/main/resources/jaas.conf
```

---

## Build & Lancement

```bash
# Build
mvn clean package

# Déployer masterannonce.war dans Tomcat
cp target/masterannonce.war $TOMCAT_HOME/webapps/
```

### Tests uniquement

```bash
# Tests unitaires seulement (rapides, sans serveur)
mvn test

# Tests d'intégration seulement
mvn failsafe:integration-test failsafe:verify

# Tous les tests
mvn verify
```

**Pourquoi séparer les tests ?**  
Les tests unitaires (`*Test.java` via surefire) n'ont aucune dépendance externe et s'exécutent en quelques secondes. Ils permettent un feedback immédiat lors du développement. Les tests d'intégration (`*IT.java` via failsafe) démarrent un serveur Grizzly2 in-process et peuvent nécessiter une base H2 ; ils sont plus lents et sont réservés à la phase `verify` (CI/CD, avant release).

---

## API

| Verbe  | Endpoint             | Auth | Description           |
| ------ | -------------------- | ---- | --------------------- |
| GET    | `/api/helloWorld`    | Non  | Test JAX-RS           |
| GET    | `/api/params?name=x` | Non  | QueryParam demo       |
| GET    | `/api/params/{val}`  | Non  | PathParam demo        |
| POST   | `/api/login`         | Non  | Login → token         |
| GET    | `/api/annonces`      | Non  | Liste paginée         |
| GET    | `/api/annonces/{id}` | Non  | Détail                |
| POST   | `/api/annonces`      | Oui  | Création              |
| PUT    | `/api/annonces/{id}` | Oui  | Mise à jour complète  |
| PATCH  | `/api/annonces/{id}` | Oui  | Mise à jour partielle |
| DELETE | `/api/annonces/{id}` | Oui  | Suppression           |

**OpenAPI UI** : `http://localhost:8080/masterannonce/api/openapi.json`

### Authentification

```bash
# 1. Login
POST /api/login
{ "username": "john", "password": "password" }
→ { "token": "uuid", "expiresIn": 3600 }

# 2. Utiliser le token
Authorization: Bearer <token>
```

---

## Règles métier

- Seul l'auteur peut modifier ou supprimer une annonce → `403 Forbidden`
- Une annonce `PUBLISHED` ne peut plus être modifiée → `409 Conflict`
- L'archivage est obligatoire avant suppression → `409 Conflict`
- Concurrence gérée via `@Version` (optimistic locking JPA)

---

## Codes HTTP

| Code | Cas                                |
| ---- | ---------------------------------- |
| 200  | Succès GET / PUT / PATCH           |
| 201  | Création réussie (POST)            |
| 204  | Suppression réussie                |
| 400  | Validation Bean Validation échouée |
| 401  | Token absent ou invalide           |
| 403  | Accès interdit (pas l'auteur)      |
| 404  | Ressource introuvable              |
| 409  | Conflit métier                     |
| 500  | Erreur serveur non interceptée     |

---

## Sécurité – Flow d'authentification

```
1. POST /api/login
   → DbLoginModule vérifie username/password en BDD (JAAS)
   → Subject peuplé : UserPrincipal + RolePrincipal
   → Token UUID généré, stocké en mémoire (TokenStore)
   → Token retourné au client

2. Requête protégée
   → Header: Authorization: Bearer <token>
   → AuthFilter (ContainerRequestFilter @Secured)
   → TokenLoginModule reconstruit le Subject depuis le token
   → SecurityContext JAX-RS custom attaché à la requête
   → Service récupère userId depuis le contexte

3. Expiration
   → Token valide 3600 secondes
   → Après expiration : 401 Unauthorized
```

---

## Problèmes rencontrés & solutions

### 1. Transactions JPA en mode RESOURCE_LOCAL

**Problème** : Sans container Jakarta EE gérant l'injection, il faut gérer manuellement `EntityManagerFactory` et les transactions (`begin/commit/rollback`).  
**Solution** : Singleton `JPAUtil` centralisant l'EMF avec un nom de PU configurable pour switcher entre PostgreSQL (prod) et H2 (tests).

### 2. JAAS sans session HTTP

**Problème** : JAAS est conçu pour les sessions Java SE. Dans un contexte REST stateless, on ne peut pas conserver le `LoginContext` entre requêtes.  
**Solution** : JAAS authentifie à la connexion (DbLoginModule), puis un token opaque (UUID) est émis. Chaque requête recrée un `LoginContext` via `TokenLoginModule` pour reconstituer l'identité, garantissant le stateless côté HTTP.

### 3. Filtre JAX-RS avec `@NameBinding`

**Problème** : Appliquer le filtre `AuthFilter` uniquement sur certains endpoints sans Spring Security.  
**Solution** : Annotation `@Secured` avec `@NameBinding`. Le filtre et les méthodes/classes annotées `@Secured` sont liés automatiquement par le runtime JAX-RS (Jersey).

### 4. Tests avec H2 au lieu de PostgreSQL

**Problème** : Les tests d'intégration repository nécessitent une base de données mais ne doivent pas dépendre d'une instance PostgreSQL externe.  
**Solution** : Un `persistence.xml` dédié dans `src/test/resources` configure H2 in-memory avec `hbm2ddl.auto=create-drop`. `JPAUtil.setPersistenceUnitName()` permet de switcher avant les tests.

### 5. Gestion centralisée des erreurs

**Problème** : Sans `ExceptionMapper`, toute exception non gérée renvoie une page HTML 500, rendant l'API inutilisable pour un client JSON.  
**Solution** : `GenericExceptionMapper` catch `Throwable` en dernier recours et retourne un JSON normalisé `{ "error": "INTERNAL_ERROR", ... }`.

### 6. Pattern Builder sur Entity et DTO

**Problème** : Les constructeurs à nombreux paramètres sont illisibles et fragiles.  
**Solution** : Inner class `Builder` sur `Annonce`, `User` et `AnnonceDTO`. `AnnonceDTO.fromEntity(Annonce)` encapsule le mapping Entity→DTO en un seul endroit.

---

## Choix technique : Jersey vs RESTEasy

**Jersey** a été choisi car :

- C'est l'implémentation de référence JAX-RS (RI)
- Excellent support avec Tomcat (jersey-container-servlet)
- `jersey-test-framework-provider-grizzly2` permet des tests d'intégration in-process sans déploiement
- Documentation abondante et mature

RESTEasy (JBoss/WildFly) aurait été pertinent dans un contexte WildFly/Quarkus.

---

## Structure du projet

```
masterannonce/
├── src/
│   ├── main/
│   │   ├── java/com/masterannonce/
│   │   │   ├── api/             # JAX-RS Resources + mappers
│   │   │   ├── dto/             # DTOs + Bean Validation
│   │   │   ├── models/          # JPA Entities
│   │   │   ├── repositories/      # JPA Repositories
│   │   │   ├── services/         # Business logic
│   │   │   ├── security/        # JAAS + Filter + TokenStore
│   │   │   ├── exceptions/       # Custom exceptions
│   │   │   └── util/            # JPAUtil
│   │   └── resources/
│   │       ├── META-INF/persistence.xml  # PostgreSQL
│   │       ├── jaas.conf
│   │       ├── logback.xml       # Structured JSON logging
│   │       └── init.sql
│   └── test/
│       ├── java/com/masterannonce/
│       │   ├── api/             # *Test.java (unit) + *IT.java (integration)
│       │   ├── repositories/      # Repository integration tests + TestDataLoader
│       │   └── security/        # JAAS module unit tests
│       └── resources/
│           └── META-INF/persistence.xml  # H2 in-memory
├── MasterAnnonce.postman_collection.json  # Collection Postman (20 requêtes)
├── load-test.sh                           # Test de charge (bash/curl)
├── load-test.ps1                          # Test de charge (PowerShell)
└── pom.xml
```

---

## Collection Postman

Le fichier `MasterAnnonce.postman_collection.json` contient 20 requêtes organisées en 5 dossiers :

| Dossier                  | Contenu                                                  |
| ------------------------ | -------------------------------------------------------- |
| 01 – HelloWorld          | Endpoints de test JAX-RS (QueryParam, PathParam)         |
| 02 – Authentification    | Login valide, mauvais password (401), champs vides (400) |
| 03 – Annonces (lecture)  | Liste paginée, détail, 404 inexistant                    |
| 04 – Annonces (écriture) | Création (201), validation (400), PUT, PATCH, 409, 401   |
| 05 – Scénario complet    | Login → Créer → Modifier → Archiver → Supprimer → 404    |

**Import** : Postman → Import → Upload Files → sélectionner le fichier JSON.

Les variables `token` et `annonceId` sont automatiquement chaînées entre les requêtes grâce aux scripts de test intégrés.

---

## Tests de charge

Scripts simples basés sur `curl` / `Invoke-WebRequest` pour valider les performances de l'API sous charge.

```bash
# Linux / macOS / Git Bash
chmod +x load-test.sh
./load-test.sh              # 50 itérations, 5 concurrents
./load-test.sh 100 10       # 100 itérations, 10 concurrents
```

```powershell
# Windows PowerShell
.\load-test.ps1                        # 50 itérations
.\load-test.ps1 -Iterations 100        # 100 itérations
```

Les résultats sont exportés dans `load-test-results.csv` (temps de réponse, codes HTTP) et un rapport synthétique est affiché dans la console.
