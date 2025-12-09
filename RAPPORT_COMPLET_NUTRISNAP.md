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

**V. STRATÉGIE DE TEST ET ASSURANCE QUALITÉ (ATELIERS 1 à 6)**
5.1. Niveaux de Test et Pyramide (ATELIER 2)
5.2. Techniques de Conception de Tests (ATELIER 4)
5.3. Tests Statiques et Revues (ATELIER 3)
5.4. Gestion des Tests et des Défauts (ATELIER 5)
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
Le projet **NutriSnap** s'inscrit dans le domaine de la **e-santé (HealthTech)** et du **bien-être numérique**. Il s'agit d'une application utilitaire mobile destinée au grand public, visant à simplifier le suivi nutritionnel quotidien grâce à l'intelligence artificielle générative.

### 1.1.2. Problématique et Besoins Fonctionnels
Dans un contexte où la gestion de l'alimentation est cruciale pour la santé (lutte contre l'obésité, diabète, performances sportives), les outils traditionnels de suivi calorique (type MyFitnessPal) présentent des frictions majeures :
*   **Saisie manuelle fastidieuse** : Peser les aliments et rechercher dans des bases de données décourage 80% des utilisateurs après 2 semaines.
*   **Manque d'accompagnement** : L'utilisateur se retrouve seul face à des chiffres sans contexte.
*   **Interface complexe** : Trop de fonctionnalités inutiles qui nuisent à l'expérience utilisateur.

**Besoins Fonctionnels Identifiés :**
1.  **Reconnaissance Visuelle** : Identifier automatiquement le contenu d'une assiette via une photo.
2.  **Calcul Automatique** : Estimer les calories et macronutriments (Protéines, Glucides, Lipides) sans saisie manuelle.
3.  **Coaching Virtuel** : Offrir un assistant conversationnel ("AI Bro") pour des conseils personnalisés.
4.  **Suivi de Progression** : Visualiser l'évolution du poids et l'atteinte des objectifs quotidiens.

### 1.1.3. Objectifs du Projet
L'objectif est de développer une application Android native (MVP - Minimum Viable Product) robuste qui :
*   Permet l'inscription et la connexion sécurisée des utilisateurs.
*   Intègre l'API Gemini (Google) pour l'analyse d'image et le chat.
*   Stocke les données de manière persistante et sécurisée dans le cloud (Supabase).
*   Offre une expérience utilisateur fluide (Material Design).

## 1.2. Présentation de l'Application
*   **Nom** : NutriSnap
*   **Vision** : "Votre nutritionniste de poche, propulsé par l'IA."
*   **Public Cible** :
    *   Sportifs (suivi précis des macros).
    *   Personnes en perte de poids (suivi calorique simplifié).
    *   Utilisateurs tech-savvy cherchant à automatiser leur "quantified self".

---

# II. MÉTHODOLOGIE DE GESTION DE PROJET : SCRUM

## 2.1. Justification du Choix de Scrum
Le développement d'une application mobile innovante intégrant de l'IA comporte des incertitudes techniques et fonctionnelles. La méthode **Agile Scrum** a été privilégiée pour :
*   **Adaptabilité** : Permettre d'ajuster les fonctionnalités (ex: affiner le prompt de l'IA) en fonction des tests.
*   **Livraison Continue** : Avoir une version testable de l'application à la fin de chaque itération.
*   **Focus Qualité** : Intégrer les tests et la revue de code au cœur du processus de développement.

## 2.2. Organisation de l'Équipe et Rôles
*   **Product Owner (PO)** :
    *   Définit la vision du produit.
    *   Gère le Product Backlog (priorisation des User Stories).
    *   Valide les fonctionnalités livrées.
*   **Scrum Master** :
    *   Garant du respect de la méthodologie Scrum.
    *   Facilitateur des cérémonies.
    *   Lève les obstacles (ex: problèmes de configuration Docker/Jenkins).
*   **Équipe de Développement** :
    *   Conception technique, développement Android (Java), tests unitaires et intégration.

## 2.3. Cérémonies Scrum
### 2.3.1. Planification des Sprints (Sprint Planning)
Réunion en début de sprint (cycle de 2 semaines) pour sélectionner les User Stories à réaliser.
*   *Exemple Sprint 1* : Authentification et Onboarding.
*   *Exemple Sprint 2* : Intégration Caméra et API Gemini.

### 2.3.2. Mêlée Quotidienne (Daily Scrum)
Point de synchronisation de 15 minutes chaque matin :
1.  Ce que j'ai fait hier (ex: "J'ai implémenté l'interface Retrofit pour Supabase").
2.  Ce que je vais faire aujourd'hui (ex: "Je vais connecter le ViewModel au LoginActivity").
3.  Mes blocages (ex: "J'ai un souci de dépendance avec CameraX").

### 2.3.3. Revue de Sprint (Sprint Review) et Démo
Présentation de l'incrément produit (l'application fonctionnelle) aux parties prenantes.
*   *Démo* : Montrer le scan d'un aliment en temps réel.

### 2.3.4. Rétrospective de Sprint (Sprint Retrospective)
Analyse de l'amélioration continue de l'équipe.
*   *Keep* : La communication fluide, l'utilisation de Git.
*   *Improve* : La gestion des erreurs API, la couverture de tests.

## 2.4. Artefacts Scrum
### 2.4.1. Product Backlog (Gestion des User Stories)
Liste ordonnée de tout ce qui est nécessaire dans le produit.
*   **US-01** : En tant qu'utilisateur, je veux créer un compte pour sauvegarder mes données. (Priorité: Haute)
*   **US-02** : En tant qu'utilisateur, je veux scanner un repas pour obtenir ses calories. (Priorité: Haute)
*   **US-03** : En tant qu'utilisateur, je veux discuter avec un coach IA. (Priorité: Moyenne)

### 2.4.2. Sprint Backlog et Définition de "Fini" (Definition of Done)
*   **Sprint Backlog** : Les tâches techniques pour réaliser les US du sprint (ex: "Créer layout activity_login.xml", "Implémenter AuthRepository").
*   **Definition of Done (DoD)** :
    *   Code compilé sans erreur.
    *   Tests unitaires passés (Green).
    *   Code review effectuée.
    *   Fonctionnalité testée sur émulateur.
    *   Build CI (Jenkins) réussi.

---

# III. ARCHITECTURE ET CHOIX TECHNOLOGIQUES

## 3.1. Choix du Langage et du Framework
*   **Langage : Java**.
    *   *Justification* : Robustesse, typage statique, vaste écosystème de bibliothèques, maîtrise de l'équipe.
*   **SDK Android**.
    *   *Min SDK* : 24 (Android 7.0) pour couvrir ~94% des appareils.
    *   *Target SDK* : 36 (Android 14) pour bénéficier des dernières optimisations de sécurité et performance.
*   **UI Framework** : XML Layouts avec **ViewBinding**.
    *   *Justification* : Séparation claire vue/logique, performance, prévention des NullPointerExceptions sur les vues.

## 3.2. Architecture Interne de l'Application
L'application suit le pattern architectural **MVVM (Model-View-ViewModel)** recommandé par Google :
*   **Model** : Couche de données (`UserProfile`, `AuthResponse`, `SupabaseService`). Indépendant de l'UI.
*   **View** : Couche d'interface (`LoginActivity`, `ScanFragment`). Observe les données du ViewModel.
*   **ViewModel** : Couche de logique de présentation (`OnboardingViewModel`). Expose les données à la vue via `LiveData` et survit aux changements de configuration (rotation d'écran).

## 3.3. Choix des Outils et Bibliothèques Clés
### 3.3.1. Base de Données
*   **Supabase (PostgreSQL)** :
    *   Utilisé comme Backend-as-a-Service.
    *   Stockage relationnel robuste pour les profils utilisateurs et journaux alimentaires.
    *   Authentification gérée (Email/Password).

### 3.3.2. Réseau et API
*   **Retrofit 2** : Client HTTP standard pour Android. Simplifie les appels REST vers Supabase.
*   **Gson** : Convertisseur JSON <-> Objets Java.
*   **Gemini Pro Vision (Google)** : Modèle d'IA multimodal pour l'analyse d'images et la génération de texte (Chatbot).

### 3.3.3. Gestion de l'État
*   **LiveData** : Observables conscients du cycle de vie Android, utilisés pour mettre à jour l'UI automatiquement quand les données changent.

## 3.4. Infrastructure de Support
*   **Docker** : Pour standardiser l'environnement de développement et de build (CI).

---

# IV. CONCEPTION ET DÉVELOPPEMENT

## 4.1. Conception Fonctionnelle
Spécifications détaillées des fonctionnalités principales :
1.  **Authentification** :
    *   Inscription avec email/mot de passe.
    *   Vérification obligatoire de l'email (lien Supabase).
    *   Gestion de session persistante (`SharedPreferences`).
2.  **Onboarding (Wizard)** :
    *   Collecte progressive des données : Nom -> Âge/Genre -> Poids/Taille -> Objectif -> Activité.
    *   Calcul du métabolisme de base (BMR) et des besoins caloriques.
3.  **Scan IA** :
    *   Utilisation de la caméra pour capturer un plat.
    *   Envoi à Gemini avec un prompt spécifique ("Analyse cette image, donne le nom du plat et les macros").
    *   Affichage structuré des résultats.

## 4.2. Conception Technique
*   **Diagramme de Classes** : Voir Annexe Visuelle.
    *   Classe `UserProfile` centralisant toutes les données physiologiques.
    *   Interface `SupabaseService` définissant les contrats d'API.
*   **Modélisation des Données** :
    *   Table `user_profiles` (Supabase) : `user_id` (PK), `full_name`, `age`, `weight`, `height`, `goal`, `macros`.

## 4.3. Conception de l'Interface Utilisateur (UI/UX)
### 4.3.1. Maquettes et Prototypage
*   Approche "Mobile First".
*   Flux utilisateur simplifié : Splash -> Welcome -> Login/Signup -> Onboarding -> Dashboard.
### 4.3.2. Respect des Lignes Directrices Material Design
*   Utilisation de `MaterialButton`, `TextInputEditText`, `CardView`.
*   Feedback utilisateur via `Toast` et `ProgressBar` (indicateurs de chargement).
*   Navigation intuitive via `BottomNavigationView`.

## 4.4. Déroulement du Développement par Sprints
*   **Sprint 1** : Structure du projet, Splash Screen, Login/Signup UI.
*   **Sprint 2** : Intégration Supabase Auth, Logique de connexion.
*   **Sprint 3** : Onboarding Wizard, Sauvegarde du profil.
*   **Sprint 4** : Intégration CameraX et API Gemini (Scan).
*   **Sprint 5** : Chatbot AI Bro, Dashboard, Tests et CI/CD.

---

# V. STRATÉGIE DE TEST ET ASSURANCE QUALITÉ (ATELIERS 1 à 6)

## 5.1. Niveaux de Test et Pyramide (ATELIER 2)
La stratégie de test respecte la pyramide des tests pour optimiser le ROI (Retour sur Investissement) :
### 5.1.1. Tests Unitaires (Unit Testing) - Base de la pyramide
*   **Objectif** : Vérifier la logique métier isolée.
*   **Outil** : JUnit 4.
*   **Exemple** : Tester que la méthode `isEmailConfirmed()` renvoie `true` si le champ date est rempli.
### 5.1.2. Tests d'Intégration - Milieu de la pyramide
*   **Objectif** : Vérifier l'interaction entre deux modules (ex: ViewModel et Repository).
*   **Exemple** : Vérifier que le ViewModel met bien à jour le LiveData après une réponse simulée de l'API.
### 5.1.3. Tests Système et UI (Espresso) - Sommet de la pyramide
*   **Objectif** : Vérifier le parcours utilisateur complet (End-to-End).
*   **Outil** : Espresso.
*   **Exemple** : Lancer l'app, saisir "test@email.com", cliquer sur "Login", vérifier que l'écran Dashboard s'affiche.

## 5.2. Techniques de Conception de Tests (ATELIER 4)
### 5.2.1. Tests Boîte Noire
Tests basés sur les spécifications fonctionnelles sans voir le code.
*   **Partitions d'Équivalence** : Pour l'âge (18-99 ans), tester une valeur valide (25), une invalide (10).
*   **Valeurs Limites** : Tester les bornes (18, 99).
### 5.2.2. Tests Boîte Blanche
Tests basés sur la structure interne du code.
*   **Couverture des instructions** : S'assurer que chaque ligne de code est exécutée au moins une fois par les tests.

## 5.3. Tests Statiques et Revues (ATELIER 3)
### 5.3.1. Application d'Android Lint
Analyse statique automatique du code pour détecter :
*   Problèmes de performance (ex: layouts trop imbriqués).
*   Problèmes d'accessibilité (ex: manque de `contentDescription` sur les images).
*   Erreurs potentielles (ex: appels réseau sur le thread principal).
### 5.3.2. Revues de Code
Processus systématique avant fusion : un autre développeur relit le code pour vérifier la lisibilité, la logique et le respect des normes.

## 5.4. Gestion des Tests et des Défauts (ATELIER 5)
### 5.4.1. Critères d'Entrée et de Sortie des Sprints
*   **Entrée** : Les US sont claires et estimées.
*   **Sortie** : Tous les tests (unitaires et UI critiques) passent.
### 5.4.2. Outil de Gestion des Défauts
Utilisation d'un tracker (type Jira ou GitHub Issues) pour loguer les bugs avec : Priorité, Sévérité, Étapes de reproduction.

## 5.5. Types de Tests Spécifiques à l'Android
### 5.5.1. Tests de Performance
Vérification de l'absence de fuites de mémoire (Memory Leaks) et de la fluidité de l'UI (60fps).
### 5.5.2. Tests de Compatibilité
Vérification sur différentes tailles d'écran (téléphone vs tablette) et versions d'Android (API 24 à 34).

---

# VI. DEVOPS ET PIPELINE CI/CD

## 6.1. Outils de Gestion de Versions
*   **Git** : Utilisé pour le versioning du code source.
*   **Dépôt** : Hébergé sur GitHub (`nutrisnap-android`).
*   **Bonnes pratiques** : Commits atomiques, messages conventionnels (`feat:`, `fix:`, `docs:`).

## 6.2. Intégration Continue (CI)
### 6.2.1. Choix de l'Outil
**Jenkins** a été choisi pour sa flexibilité et sa capacité à s'exécuter en local ou sur serveur privé.
### 6.2.2. Processus d'Intégration
Le fichier `Jenkinsfile` définit le pipeline qui se déclenche automatiquement :
1.  **Checkout** : Récupération du code.
2.  **Lint** : Analyse statique.
3.  **Test** : Exécution des tests unitaires.
4.  **Build** : Compilation du projet.
Si une étape échoue, le développeur est notifié et le code n'est pas validé.

## 6.3. Déploiement Continu (CD)
### 6.3.1. Containerisation de l'Environnement de Build (Dockerfile)
Pour éviter le syndrome "ça marche sur ma machine", l'environnement de build est dockerisé :
*   Image de base : Ubuntu 22.04.
*   Outils : JDK 17, Android SDK 36, Gradle.
Cela garantit un environnement identique pour tous les développeurs et le serveur CI.

### 6.3.2. Processus de Build Android
Le pipeline automatise la génération des livrables :
*   **Compilation** : `./gradlew assembleRelease`.
*   **Signature** : Utilisation d'un `Keystore` sécurisé (injecté via les Credentials Jenkins) pour signer l'APK.
*   **Archivage** : Sauvegarde des APKs générés (Debug et Release) comme artefacts de build.

### 6.3.3. Automatisation du Déploiement
Bien que non activé pour ce projet étudiant, l'étape suivante serait l'utilisation de **Fastlane** pour automatiser l'envoi des captures d'écran et de l'APK vers la Google Play Console (pistes Alpha/Bêta).

## 6.4. Monitoring et Suivi Post-Déploiement
Intégration théorique de **Firebase Crashlytics** pour remonter automatiquement les crashs survenant chez les utilisateurs finaux, permettant une réactivité maximale de l'équipe de développement.

---

# VII. CONCLUSION ET PERSPECTIVES

## 7.1. Bilan du Projet et Atteinte des Objectifs
Le projet NutriSnap est un succès technique et fonctionnel. L'application permet :
*   Une inscription fluide et sécurisée.
*   Un onboarding complet.
*   Une utilisation innovante de l'IA pour la nutrition.
Les objectifs initiaux du MVP sont atteints.

## 7.2. Défis Rencontrés et Solutions Apportées
*   **Complexité de l'API Gemini** : La gestion des prompts pour obtenir un JSON structuré a demandé plusieurs itérations. *Solution : Affinage des prompts et parsing robuste.*
*   **Pipeline CI/CD** : Configurer l'environnement Android dans Docker (licences SDK) a été complexe. *Solution : Utilisation d'une image Docker optimisée et script d'acceptation des licences.*

## 7.3. Améliorations Futures
*   **Mode Offline** : Permettre la consultation du journal sans internet.
*   **Scan Code-Barres** : Ajouter une alternative au scan visuel pour les produits industriels.
*   **Notifications** : Rappels pour les repas et l'hydratation.

## 7.4. Apports Personnels et Leçons Apprises
Ce projet a permis de mettre en pratique :
*   La méthodologie **Scrum** dans un contexte réel.
*   L'importance de la **Qualité Logicielle** (Tests, Lint).
*   La puissance du **DevOps** pour automatiser les tâches répétitives et sécuriser les livraisons.
*   L'intégration de technologies de pointe (**IA Générative**) dans une application mobile.
