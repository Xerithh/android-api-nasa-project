# NASA Space Gallery

Application Android, permettant d'explorer la photothèque spatiale de la NASA via son API publique, de consulter les détails de chaque prise de vue et de gérer une liste de favoris synchronisée localement.

---

## Stack technique

| Domaine | Technologie / Librairie | Version | Role |
|---|---|---|---|
| Langage | Kotlin | 2.2.10 | Langage principal du projet |
| UI Toolkit | Jetpack Compose / Material 3 | BOM 2025.02.00 | Construction declarative de l'interface utilisateur |
| Navigation | Navigation 3 (AndroidX) | 1.3.0-alpha01 | Nouvelle bibliotheque de navigation Compose basee sur un backstack type-safe |
| Client HTTP | Ktor Client (CIO) | 3.1.1 | Appels reseau asynchrones vers l'API NASA Images |
| Serialisation | Kotlinx Serialization | 1.8.0 | Parsing JSON des reponses d'API |
| Base de donnees | Room | 2.7.0-alpha13 | Persistance locale SQLite des favoris avec support Flow |
| Chargement d'images | Coil Compose | 2.7.0 | Telechargement, mise en cache et affichage asynchrone des images |
| Concurrence | Kotlin Coroutines & Flow | 1.10.1 | Traitements asynchrones et flux de donnees reactifs |
| Architecture | Android Lifecycle & ViewModel | 2.8.7 | Gestion d'etat MVVM et survie aux changements de configuration |
| Build System | Gradle avec Version Catalog | AGP 9.4.1 | Gestion declarative des dependances (`libs.versions.toml`) |

---

## Architecture du projet

Le projet suit les principes de l'architecture recommandee par Android (MVVM + Repository) :

```text
com.example.myapiapp/
├── data/
│   ├── local/          # Base de donnees Room (Entites, DAO, Converters)
│   ├── remote/         # Modeles Ktor et configuration client HTTP
│   └── repository/     # ApodRepository (orchestration reseau + base locale)
├── ui/
│   ├── components/     # Composants Compose reutilisables (cartes, etats de chargement)
│   ├── navigation/     # Configuration Navigation 3 (destinations et backstack)
│   ├── screens/        # Ecrans complets (HomeScreen, DetailScreen, FavoritesScreen)
│   ├── theme/          # Couleurs, typographies et theme Material 3
│   └── viewmodel/      # ApodViewModel et etats d'interface scelles (UiState)
└── MainActivity.kt     # Point d'entree de l'application
```

---

## API utilisee

L'application consomme l'API publique NASA Image and Video Library :
- Endpoint : `https://images-api.nasa.gov/search`
- Requete par defaut : recherche thematique spatiale filtree sur les images.
- Aucune cle d'API obligatoire pour ce endpoint public.

---

## Installation et lancement

### Prerequis

- Android Studio Ladybug ou version plus recente.
- JDK 17 ou supérieur (JDK 21/24 recommande).
- Appareil de test ou emulateur sous Android 7.0 (API 24) minimum.

### Execution

1. Cloner le depot :
   ```bash
   git clone https://github.com/Xerithh/android-api-nasa-project.git
   cd android-api-nasa-project
   ```

2. Synchroniser le projet avec Gradle via Android Studio.

3. Compiler et lancer sur votre terminal

