# Application de Gestion d'Agence de Recrutement

Application Spring Boot avec JavaFX pour la gestion d'une agence de recrutement.

## Technologies utilisées

- **Spring Boot 3.2.0**
- **JavaFX 21**
- **MySQL**
- **Spring Data JPA**
- **Lombok**
- **Spring Security**

## Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- MySQL 8.0+
- MySQL Workbench ou un autre client MySQL

## Configuration de la base de données

1. Créer une base de données MySQL :
```sql
CREATE DATABASE agence_recrutement;
```

2. Configurer les paramètres de connexion dans `src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/agence_recrutement?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe
```

## Installation et exécution

1. Cloner le projet ou extraire les fichiers
2. Dans le terminal, naviguer vers le dossier du projet
3. Compiler le projet :
```bash
mvn clean install
```

4. Lancer l'application :
```bash
mvn spring-boot:run
```

Ou depuis votre IDE, exécutez la classe `AgencerecrutementApplication`.

## Utilisation

### Compte administrateur par défaut

À la première exécution, un compte administrateur est créé automatiquement :
- **Login** : `admin`
- **Mot de passe** : `1234567890`

⚠️ **Important** : Changez ce mot de passe après la première connexion !

### Fonctionnalités principales

#### Pour l'Administrateur
- Gestion des utilisateurs
- Gestion des journaux et catégories
- Consultation des statistiques
- Historique des recrutements

#### Pour l'Entreprise
- Souscription d'abonnements aux journaux
- Création et publication d'offres d'emploi
- Consultation des candidatures
- Recrutement de candidats

#### Pour le Demandeur d'emploi
- Consultation des offres disponibles
- Candidature aux offres (sous conditions)
- Suivi des candidatures

## Règles métier implémentées

1. **Abonnements** : Une entreprise ne peut avoir qu'un seul abonnement actif par journal
2. **Publication** : Les offres ne peuvent être publiées que via un abonnement actif
3. **Candidatures** : 
   - Le demandeur doit avoir au moins l'expérience requise
   - Un demandeur ne peut postuler qu'une seule fois à une même offre
4. **Recrutements** : 
   - Limité au nombre de postes disponibles
   - Désactivation automatique de l'offre quand tous les postes sont pourvus

## Structure du projet

```
src/main/java/com/example/agencerecrutement/
├── model/              # Entités JPA
├── repository/         # Repositories Spring Data
├── service/            # Services métier
├── javafx/             # Interface JavaFX
│   └── controllers/    # Contrôleurs JavaFX
└── config/             # Configuration Spring
```

## Développement futur

- Implémentation complète des dialogues JavaFX
- Amélioration de l'interface utilisateur
- Ajout de fonctionnalités de recherche et filtrage
- Export de rapports
- Notifications

## Auteurs
hiba zouitina
imane taleb
saida stifi
chouaib bouslamti

Projet développé dans le cadre d'un mini-projet Spring Boot + JavaFX


