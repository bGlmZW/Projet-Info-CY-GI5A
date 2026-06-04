# Projet Génie Logiciel — Simulation d'agents dans un graphe

Application JavaFX permettant de visualiser le déplacement d'agents dans un graphe pondéré. Les agents se déplacent de nœud en nœud en suivant un chemin calculé par un algorithme de plus court chemin (Dijkstra ou A*).

---

## Prérequis

- **JDK 21** — [Télécharger sur adoptium.net](https://adoptium.net/)
- **JavaFX 21 SDK** — [Télécharger sur gluonhq.com](https://gluonhq.com/products/javafx/) (choisir la version correspondant à votre OS)

> JavaFX n'est plus inclus dans le JDK depuis Java 11, il faut le télécharger séparément.

---

## Installation et lancement

### Avec Eclipse

1. Cloner le dépôt :
```bash
   git clone <url-du-repo> ProjetGenieLogiciel
```

2. Dans Eclipse : **File → New → Java Project**
   - Décocher **"Use default location"**
   - Dans Location, pointer vers le dossier cloné
   - Cliquer **Next** puis **Finish**

3. Configurer JavaFX dans le Build Path :
   - Clic droit sur le projet → **Build Path → Configure Build Path**
   - Onglet **Libraries → Modulepath → Add External JARs**
   - Sélectionner tous les `.jar` dans le dossier `lib/` du SDK JavaFX téléchargé
   - **Apply and Close**

4. Mettre à jour le fichier `src/module-info.java` — Eclipse en génère un vide
   au moment de la création du projet, remplacer son contenu par :
```java
   module <nom_du_projet> {
       requires transitive javafx.controls;
       requires transitive javafx.graphics;

       exports fr.projet;
       exports fr.projet.model;
       exports fr.projet.pathfinding;
       exports fr.projet.simulation;
       exports fr.projet.ui;
       exports fr.projet.controller;
       exports fr.projet.view;
   }
```
   En remplaçant `<nom_du_projet>` par le nom généré par Eclipse (visible en haut du fichier).

5. Lancer `MainApp.java` (dans `src/fr/projet/ui/`) via **Run As → Java Application**

---

### En ligne de commande

> Remplacer `/chemin/vers/javafx-sdk-21` par le chemin réel sur votre machine.

**Linux / macOS**

```bash
# Compiler
javac --module-path /chemin/vers/javafx-sdk-21/lib \
      --add-modules javafx.controls,javafx.graphics \
      -d bin \
      $(find src -name "*.java")

# Lancer
java --module-path /chemin/vers/javafx-sdk-21/lib \
     --add-modules javafx.controls,javafx.graphics \
     -cp bin fr.projet.ui.MainApp
```

**Windows (PowerShell)**

```powershell
# Compiler
javac --module-path "C:\chemin\vers\javafx-sdk-21\lib" `
      --add-modules javafx.controls,javafx.graphics `
      -d bin `
      (Get-ChildItem -Recurse -Filter "*.java" src).FullName

# Lancer
java --module-path "C:\chemin\vers\javafx-sdk-21\lib" `
     --add-modules javafx.controls,javafx.graphics `
     -cp bin fr.projet.ui.MainApp
```

---

## Utilisation

Au lancement, un graphe de démonstration est affiché. La barre d'outils en haut permet d'éditer le graphe, la barre de contrôle en bas pilote la simulation.

### Édition du graphe

| Bouton | Action |
|---|---|
| Add Node | Cliquer sur une zone vide pour créer un nœud |
| Add Edge | Cliquer sur deux nœuds pour créer une arête (poids demandé) |
| Add Agent | Sélectionner un nœud, puis renseigner la destination, la vitesse et l'algorithme |
| Delete | Cliquer sur un nœud ou une arête pour le supprimer |

### Contrôle de la simulation

| Bouton | Action |
|---|---|
| Start | Lancer la simulation en continu (1 tick/seconde) |
| Pause | Mettre en pause |
| Next Tick | Avancer d'un tick manuellement |
| Reset | Remettre la simulation à zéro |

### Algorithmes disponibles

Lors de la création d'un agent, deux algorithmes de pathfinding sont proposés :

- **Dijkstra** — garantit le chemin le plus court sur graphe pondéré
- **A\*** — plus rapide en pratique grâce à une heuristique, résultat identique à Dijkstra sur ce type de graphe

---

## Structure du projet

```
src/
└── fr/projet/
    ├── Main.java                  # Point d'entrée console (tests)
    ├── controller/
    │   ├── GraphController.java   # Gestion des interactions sur le graphe
    │   └── SimulationController.java
    ├── model/
    │   ├── Agent.java             # Agent de base
    │   ├── AgentFactory.java      # Fabrique d'agents
    │   ├── AgentType.java         # Types d'agents disponibles
    │   ├── FastAgent.java         # Agent rapide
    │   ├── NormalAgent.java       # Agent normal
    │   ├── SlowAgent.java         # Agent lent
    │   ├── CargoAgent.java        # Agent cargo
    │   ├── PriorityAgent.java     # Agent prioritaire
    │   ├── Edge.java              # Arête du graphe
    │   ├── Graph.java             # Structure du graphe
    │   ├── Node.java              # Nœud du graphe
    │   └── State.java             # États possibles d'un agent
    ├── pathfinding/
    │   ├── PathFinder.java        # Interface commune
    │   ├── DijkstraPathFinder.java
    │   ├── AStarPathFinder.java
    │   ├── PathFinderFactory.java
    │   └── PathFinderType.java
    ├── simulation/
    │   └── SimulationEngine.java  # Moteur de simulation tick-based
    ├── ui/
    │   ├── MainApp.java           # Point d'entrée JavaFX
    │   ├── ToolBox.java
    │   ├── SimulationBar.java
    │   └── HelpDialog.java
    └── view/
        └── GraphView.java         # Rendu visuel du graphe
```

---

## Javadoc

La documentation générée est disponible dans le dossier `doc/`. Ouvrir `doc/index.html` dans un navigateur.
