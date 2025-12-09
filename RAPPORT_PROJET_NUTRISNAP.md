# RAPPORT DE PROJET : NUTRISNAP
## Application Android de Suivi Nutritionnel Assisté par IA

---

## TABLE DES MATIÈRES

**I. INTRODUCTION GÉNÉRALE**
1.1. Contexte du Projet
1.1.1. Domaine d'Application
1.1.2. Problématique et Besoins Fonctionnels
1.1.3. Objectifs du Projet
1.2. Présentation de l'Application

**II. MÉTHODOLOGIE DE GESTION DE PROJET : SCRUM**
2.1. Justification du Choix de Scrum
2.2. Organisation de l'Équipe et Rôles
2.3. Cérémonies Scrum
2.4. Artefacts Scrum

**III. ARCHITECTURE ET CHOIX TECHNOLOGIQUES**
3.1. Choix du Langage et du Framework
3.2. Architecture Interne de l'Application
3.3. Choix des Outils et Bibliothèques Clés
3.4. Infrastructure de Support

**IV. CONCEPTION ET DÉVELOPPEMENT**
4.1. Conception Fonctionnelle
4.2. Conception Technique
4.3. Conception de l'Interface Utilisateur (UI/UX)
4.4. Déroulement du Développement par Sprints

**V. STRATÉGIE DE TEST ET ASSURANCE QUALITÉ**
5.1. Niveaux de Test et Pyramide
5.2. Techniques de Conception de Tests
5.3. Tests Statiques et Revues
5.4. Gestion des Tests et des Défauts
5.5. Types de Tests Spécifiques à l'Android

**VI. DEVOPS ET PIPELINE CI/CD**
6.1. Outils de Gestion de Versions
6.2. Intégration Continue (CI)
6.3. Déploiement Continu (CD)
6.4. Monitoring et Suivi Post-Déploiement

**VII. CONCLUSION ET PERSPECTIVES**
7.1. Bilan du Projet
7.2. Défis Rencontrés et Solutions
7.3. Améliorations Futures
7.4. Apports Personnels

---

# I. INTRODUCTION GÉNÉRALE

## 1.1. Contexte du Projet

### 1.1.1. Domaine d'Application
Le projet **NutriSnap** s'inscrit dans le domaine de la **e-santé (HealthTech)** et du **bien-être numérique**. Il s'agit d'une application utilitaire mobile destinée au grand public, visant à simplifier le suivi nutritionnel quotidien grâce à l'intelligence artificielle.

### 1.1.2. Problématique et Besoins Fonctionnels
Dans un contexte mondial où l'obésité et les maladies liées à l'alimentation sont en hausse, le suivi de son alimentation est devenu une préoccupation majeure. Cependant, les solutions existantes présentent plusieurs frictions :
*   **Saisie fastidieuse** : Devoir peser ses aliments et chercher manuellement dans des bases de données décourage les utilisateurs.
*   **Manque de personnalisation** : Les conseils sont souvent génériques.
*   **Perte de motivation** : L'interface est souvent austère et purement comptable.

**Besoins identifiés :**
*   Automatiser la reconnaissance des plats via la caméra.
*   Calculer instantanément les macronutriments (protéines, glucides, lipides).
*   Offrir un coaching personnalisé et motivant ("AI Bro").
*   Suivre l'évolution du poids et des objectifs.

### 1.1.3. Objectifs du Projet
L'objectif principal est de développer une application Android native robuste et intuitive qui permet :
1.  **L'analyse d'image** : Identifier le contenu d'une assiette par simple photo.
2.  **Le suivi intelligent** : Enregistrer automatiquement les repas dans un journal.
3.  **L'accompagnement** : Fournir un assistant virtuel (Chatbot) pour répondre aux questions nutritionnelles.
4.  **La fiabilité** : Assurer la persistance des données et la sécurité des comptes utilisateurs.

## 1.2. Présentation de l'Application
*   **Nom** : NutriSnap
*   **Vision** : "La nutrition simplifiée en un clic."
*   **Public Cible** : Personnes souhaitant perdre du poids, sportifs surveillant leurs macros, ou toute personne soucieuse de son équilibre alimentaire sans contrainte technique.

---

# II. MÉTHODOLOGIE DE GESTION DE PROJET : SCRUM

## 2.1. Justification du Choix de Scrum
Le développement mobile nécessite une grande flexibilité pour s'adapter aux retours utilisateurs et aux contraintes techniques (API, hardware). La méthode **Agile Scrum** a été choisie pour :
*   Son approche itérative permettant de livrer des fonctionnalités testables rapidement.
*   Sa capacité à gérer les changements de priorités (ex: ajout de la fonctionnalité "AI Bro").
*   L'amélioration continue de la qualité du code.

## 2.2. Organisation de l'Équipe et Rôles
*   **Product Owner (PO)** : Définit la vision du produit et priorise le Backlog (les fonctionnalités clés comme le scan ou le chat).
*   **Scrum Master** : Facilite les cérémonies et lève les obstacles techniques (ex: configuration de l'environnement Docker).
*   **Équipe de Développement** : Conçoit, développe et teste l'application Android (Java/XML).

## 2.3. Cérémonies Scrum
### 2.3.1. Planification des Sprints (Sprint Planning)
Définition des objectifs sur des cycles de 2 semaines. Exemple : "Sprint 1 : Authentification et Onboarding".
### 2.3.2. Mêlée Quotidienne (Daily Scrum)
Point rapide de 15 min pour synchroniser l'avancement et identifier les blocages (ex: crash lors du signup).
### 2.3.3. Revue de Sprint (Sprint Review)
Démonstration de l'application fonctionnelle à la fin de chaque itération.
### 2.3.4. Rétrospective de Sprint
Analyse de ce qui a bien fonctionné (ex: intégration rapide de Supabase) et des axes d'amélioration (ex: gestion des erreurs API).

## 2.4. Artefacts Scrum
### 2.4.1. Product Backlog
Liste hiérarchisée des User Stories (US), par exemple :
*   *US-01* : En tant qu'utilisateur, je veux m'inscrire pour sauvegarder mes données.
*   *US-02* : En tant qu'utilisateur, je veux scanner mon repas pour connaître ses calories.
### 2.4.2. Sprint Backlog et Definition of Done (DoD)
Une tâche est "Finie" si : Le code est commité, les tests unitaires passent, et l'APK est généré via la CI.

---

# III. ARCHITECTURE ET CHOIX TECHNOLOGIQUES

## 3.1. Choix du Langage et du Framework
*   **Langage** : **Java**. Choisi pour sa robustesse, sa maturité dans l'écosystème Android et la maîtrise de l'équipe.
*   **SDK Android** : Target SDK 36 (Android 14+), Min SDK 24 (Android 7.0) pour une large compatibilité.
*   **UI** : XML Layouts avec **ViewBinding** pour une séparation claire entre la vue et la logique.

## 3.2. Architecture Interne de l'Application
L'application suit le modèle **MVVM (Model-View-ViewModel)** (ou une variante adaptée) pour assurer la maintenabilité :
*   **View (Activity/Fragment)** : Gère l'affichage (ex: `ScanFragment`, `LoginActivity`).
*   **ViewModel** : Gère la logique de présentation et l'état (ex: `OnboardingViewModel`).
*   **Model/Repository** : Gère les données (`UserProfile`, `AuthResponse`).

## 3.3. Choix des Outils et Bibliothèques Clés
### 3.3.1. Base de Données et Backend
*   **Supabase** : Utilisé comme Backend-as-a-Service (BaaS).
    *   *Auth* : Gestion des utilisateurs (Inscription, Connexion, Confirmation Email).
    *   *Database (PostgreSQL)* : Stockage des profils utilisateurs (`user_profiles`) et des journaux.
### 3.3.2. Réseau et API
*   **Retrofit 2** : Client HTTP type-safe pour communiquer avec l'API Supabase.
*   **Gson** : Sérialisation/Désérialisation JSON.
*   **Gemini API (Google)** : Intelligence Artificielle générative pour :
    *   L'analyse d'images (reconnaissance des plats).
    *   Le chatbot "AI Bro" (conseils nutritionnels).
### 3.3.3. Multimédia
*   **CameraX** : API moderne pour la gestion de la caméra (Scan).
*   **Glide** : Chargement et mise en cache des images.

## 3.4. Infrastructure de Support
*   **Docker** : Pour standardiser l'environnement de build (voir section DevOps).

---

# IV. CONCEPTION ET DÉVELOPPEMENT

## 4.1. Conception Fonctionnelle (User Stories Clés)
1.  **Onboarding Wizard** : Un flux étape par étape pour collecter les données physiologiques (âge, poids, objectif).
2.  **Authentification Sécurisée** : Inscription avec vérification d'email obligatoire pour garantir la qualité des comptes.
3.  **Scan de Repas** : Capture photo -> Envoi à Gemini -> Réception des macros -> Validation utilisateur.
4.  **AI Bro Chat** : Interface de messagerie instantanée avec une IA spécialisée en fitness.

## 4.2. Conception Technique
*   **Modélisation des Données (`UserProfile`)** :
    *   Champs : `userId`, `fullName`, `currentWeight`, `targetWeight`, `macros` (protéines, glucides, lipides).
*   **Services** :
    *   `SupabaseService` : Interface Retrofit définissant les endpoints (`/auth/v1/signup`, `/rest/v1/user_profiles`).
    *   `GeminiService` : Gestion des prompts et des réponses de l'IA.

## 4.3. Conception de l'Interface Utilisateur (UI/UX)
### 4.3.1. Maquettes
L'interface a été pensée pour être épurée ("Clean UI").
*   **Dashboard** : Vue synthétique des calories consommées vs objectif.
*   **Navigation** : BottomNavigationBar pour un accès rapide aux fonctionnalités principales (Journal, Scan, Chat, Profil).
### 4.3.2. Material Design
Utilisation des composants Material (Cards, Floating Action Buttons, InputLayouts) pour une expérience native cohérente.

## 4.4. Déroulement du Développement
*   **Incrément 1** : Mise en place de l'architecture et de la navigation de base.
*   **Incrément 2** : Intégration de Supabase (Auth & DB).
*   **Incrément 3** : Développement du module CameraX et intégration Gemini.
*   **Incrément 4** : Finalisation de l'Onboarding et correction des bugs critiques (Crash Signup).

---

# V. STRATÉGIE DE TEST ET ASSURANCE QUALITÉ

## 5.1. Niveaux de Test et Pyramide
### 5.1.1. Tests Unitaires
Tests de la logique métier isolée (ex: calculs des macros, parsing JSON) avec **JUnit 4**.
### 5.1.2. Tests d'Intégration
Vérification de la communication entre les composants (ex: ViewModel <-> Repository).
### 5.1.3. Tests Système et UI
Tests de parcours utilisateurs complets avec **Espresso** (ex: lancer l'app, cliquer sur Login, remplir les champs).

## 5.2. Techniques de Conception de Tests
*   **Boîte Noire** : Tests basés sur les spécifications (ex: vérifier que l'inscription échoue si l'email est invalide).
*   **Boîte Blanche** : Analyse de la couverture de code pour s'assurer que toutes les branches conditionnelles (`if/else`) sont testées.

## 5.3. Tests Statiques et Revues
### 5.3.1. Android Lint
Utilisation de l'outil **Lint** intégré à Android Studio pour détecter :
*   Les problèmes de performance.
*   Les problèmes d'accessibilité.
*   Les erreurs de sécurité (ex: clés API en dur).
### 5.3.2. Revues de Code
Chaque fonctionnalité est revue avant d'être intégrée à la branche `main` (Pull Requests).

## 5.4. Gestion des Tests
Utilisation de la CI (Jenkins) pour exécuter automatiquement les tests à chaque modification du code.

---

# VI. DEVOPS ET PIPELINE CI/CD

## 6.1. Outils de Gestion de Versions
*   **Git** : Gestionnaire de versions décentralisé.
*   **GitHub** : Hébergement du code source (`nutrisnap-android`).
*   **Stratégie de branche** : `main` pour la production, branches de fonctionnalités (`feat/login`, `fix/crash`).

## 6.2. Intégration Continue (CI)
Mise en place d'un serveur **Jenkins** automatisé.
### 6.2.1. Pipeline Jenkins (Jenkinsfile)
Le pipeline est défini "as code" et comprend les étapes suivantes :
1.  **Checkout** : Récupération du code.
2.  **Environment Setup** : Vérification du JDK 17 et Android SDK 36.
3.  **Dependencies** : Téléchargement des dépendances Gradle.
4.  **Lint Analysis** : Analyse statique de la qualité.
5.  **Unit Tests** : Exécution des tests JUnit.

## 6.3. Déploiement Continu (CD)
### 6.3.1. Containerisation (Docker)
Création d'une image Docker personnalisée (`nutrisnap-builder`) contenant :
*   Ubuntu 22.04
*   OpenJDK 17
*   Android Command Line Tools & Build Tools 34.0.0
Ceci garantit que l'environnement de build est reproductible partout.

### 6.3.2. Processus de Build et Signature
Le pipeline gère automatiquement :
1.  **Build Debug** : Génération de l'APK de test.
2.  **Build Release** : Génération de l'APK optimisé.
3.  **Signature (Signing)** : Signature automatique de l'APK Release avec un Keystore sécurisé (géré via les "Credentials" Jenkins) pour garantir l'authenticité de l'application.
4.  **Archivage** : Stockage des artefacts (APK, AAB) pour téléchargement.

## 6.4. Monitoring
Utilisation des rapports générés par Jenkins (Rapport de tests HTML, Rapport Lint) pour surveiller la santé du projet.

---

# VII. CONCLUSION ET PERSPECTIVES

## 7.1. Bilan du Projet
Le projet NutriSnap a permis de livrer une application fonctionnelle répondant aux besoins modernes de suivi nutritionnel. L'intégration de l'IA (Gemini) apporte une réelle valeur ajoutée par rapport aux applications classiques.

## 7.2. Défis Rencontrés et Solutions
*   **Challenge** : Crash lors de l'inscription et gestion de l'état asynchrone.
    *   *Solution* : Refonte du flux d'authentification pour attendre la confirmation email et vérification de la complétude du profil au login.
*   **Challenge** : Configuration de l'environnement CI/CD Android.
    *   *Solution* : Création d'un Dockerfile spécifique incluant toutes les licences et outils SDK nécessaires.

## 7.3. Améliorations Futures
*   **Mode Hors-ligne** : Utilisation de Room pour mettre en cache les données.
*   **Gamification** : Ajout de badges et de défis sociaux.
*   **Déploiement Store** : Automatisation de l'upload vers le Play Store via l'API Google Play Publisher.

## 7.4. Apports Personnels
Ce projet a permis de consolider les compétences en :
*   Développement Android natif avancé.
*   Intégration d'API tierces et d'IA.
*   Mise en place d'une chaîne DevOps professionnelle complète (Docker, Jenkins, CI/CD).
