# MasterAnnonce - Modernisation JPA & Sécurisation

Ce projet est une application Java EE de gestion d'annonces immobilières, refactorisée pour utiliser **JPA/Hibernate 6** et sécurisée avec un contrôle d'accès par auteur.

## 1. Architecture Technique
L'application respecte une séparation stricte des responsabilités :
* **Modèle** : Entités JPA (`Annonce`, `User`, `Category`) avec Bean Validation.
* **Persistance** : Repositories utilisant JPQL et `LEFT JOIN FETCH`.
* **Métier** : `AnnonceService` gérant les transactions et la logique d'autorisation.
* **Web** : Servlets, Filtre de sécurité et JSTL pour l'affichage.

## 2. Solutions aux Problématiques Critiques

### Sécurisation des Accès (Autorisation)
Contrairement à une authentification simple, nous avons implémenté une vérification de propriété (**Autorisation**) :
* Un utilisateur ne peut modifier, publier ou supprimer **que ses propres annonces**.
* Le filtrage est effectué en base de données : la liste globale ne montre les brouillons (`DRAFT`) que s'ils appartiennent à l'utilisateur connecté.

### Optimisation des Performances (Lazy Loading)
Pour éviter les erreurs `LazyInitializationException` et le problème de performance **N+1 select**, toutes les jointures vers l'auteur et la catégorie sont chargées via des `FETCH JOIN` dans le Repository.

### Fiabilité des Données
L'utilisation du **try-with-resources** garantit la fermeture automatique des `EntityManager` et évite les fuites de mémoire sur le serveur Tomcat.

## 3. Stratégie de Tests (4 Niveaux)

L'application a été validée par une suite de tests automatisés (JUnit 5 & Mockito) :

1.  **Niveau 1 (Intégration Repository)** : Validation du CRUD et de la recherche paginée sur une base PostgreSQL réelle.
2.  **Niveau 2 (Unitaires Service)** : Utilisation de **Mockito** pour tester les règles métier isolées (ex: changement de statut).
3.  **Niveau 3 (Intégration Métier)** :
    * **3.a** : Enchaînement complet Création → Publication → Recherche.
    * **3.b** : Test de non-régression sur le Lazy Loading (accès aux relations après détachement de l'entité).
4.  **Niveau 4 (Web & Sécurité)** :
    * Test du `AuthFilter` (redirection des non-connectés).
    * Test d'autorisation (blocage des tentatives de modification d'annonces tierces).