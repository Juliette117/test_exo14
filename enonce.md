# TP — TDD, BDD et Reporting de Tests en Java/Maven

# MédiaCity — Gestion des prêts et réservations

## Contexte

Vous intégrez l'équipe de développement de **MédiaCity**, un éditeur de logiciels pour les médiathèques municipales.

Votre mission consiste à développer un module permettant de gérer les prêts d'ouvrages, les retours, les pénalités de retard ainsi que les réservations lorsqu'un ouvrage est indisponible.

Le projet devra respecter des exigences de qualité logicielle élevées. Toutes les fonctionnalités devront être testées automatiquement et les rapports de tests devront être générés lors de la construction du projet.

---

# Objectifs

Vous devrez réaliser un projet Maven permettant de :

- développer une logique métier en TDD ;
- formaliser des scénarios métier en BDD avec Cucumber ;
- utiliser JUnit 5, AssertJ et Mockito ;
- générer des rapports de tests et de couverture ;
- produire un site Maven regroupant les rapports du projet.

---

# Environnement technique

Le projet devra utiliser :

- Java 17 ou supérieur
- Maven 3.9+
- JUnit 5
- AssertJ
- Mockito
- Cucumber
- JaCoCo


---

# Travail demandé

## Partie 1 — Gestion des prêts

Développer un moteur de gestion des prêts répondant aux règles suivantes.

### Création d'un prêt

- création d'un prêt pour un adhérent et un ouvrage ;
- calcul automatique de la date de retour (21 jours).

### Disponibilité

- un ouvrage déjà emprunté ne peut pas être prêté une seconde fois.

### Pénalités

- calcul automatique des pénalités de retard ;
- 0,15 € par jour de retard.

### Suspension

Un adhérent ayant cumulé trois retards importants dans l'année devient suspendu et ne peut plus effectuer de nouvel emprunt.

---

## Partie 2 — Réservations (BDD)

Écrire les scénarios Gherkin permettant de couvrir les cas suivants :

- réservation d'un ouvrage indisponible ;
- plusieurs réservations sur le même ouvrage ;
- restitution d'un ouvrage réservé ;
- refus d'une réservation pour un adhérent suspendu.

Les scénarios devront être exécutables avec Cucumber.

Les Step Definitions devront être développées en Java.

Ajouter au minimum un scénario supplémentaire validant une règle métier pertinente.

---

## Partie 3 — Reporting

Configurer Maven afin de générer automatiquement :

- les rapports de tests Surefire ;
- le rapport de couverture JaCoCo ;
- le rapport Cucumber


La couverture minimale attendue est de **80 %** sur la logique métier.

---

# Contraintes

Le développement devra respecter les règles suivantes :

- utilisation du cycle TDD ;
- tests unitaires avec JUnit 5 ;
- assertions avec AssertJ ;
- utilisation de Mockito lorsque des dépendances doivent être simulées ;
- code organisé et lisible ;
- nommage explicite des tests.

---

# Livrables

À la fin du TP, le dépôt devra contenir :

- le projet Maven complet ;
- les tests unitaires ;
- les scénarios Cucumber ;
- les Step Definitions ;
- les rapports générés ;

