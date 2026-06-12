# Projet Génie Logiciel — Simulation d'agents dans un graphe

Application JavaFX permettant de visualiser le déplacement d'ambulances dans un graphe pondéré. Elles se déplacent de nœud en nœud en suivant un chemin calculé par un algorithme de plus court chemin (Dijkstra ou A*).

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

    	exports model.accident;
    	exports model.agent;
    	exports model.graph;
    	exports pathfinding;
    	exports simulation;
    	exports controller;
    	exports view;
    	exports io;
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
     -cp bin view.MainApp
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
├── README.md
├── UML_UseCase_Diagram_Projet_info_GIA.pdf
├── simulation_save.dat
└── src
    ├── controller
    │   ├── AgentFactory.java
    │   ├── GraphController.java
    │   ├── PathFinderFactory.java
    │   └── SimulationController.java
    ├── io
    │   └── GraphStorageManager.java
    ├── model
    │   ├── accident
    │   │   ├── Accident.java
    │   │   └── AccidentType.java
    │   ├── agent
    │   │   ├── Agent.java
    │   │   ├── AgentType.java
    │   │   ├── CargoAgent.java
    │   │   ├── FastAgent.java
    │   │   ├── NormalAgent.java
    │   │   ├── Patient.java
    │   │   ├── PriorityAgent.java
    │   │   ├── SlowAgent.java
    │   │   └── State.java
    │   └── graph
    │       ├── Edge.java
    │       ├── EdgeType.java
    │       ├── Graph.java
    │       ├── Node.java
    │       └── NodeType.java
    ├── module-info.java
    ├── pathfinding
    │   ├── AStarPathFinder.java
    │   ├── CongestionAwarePathFinder.java
    │   ├── DijkstraPathFinder.java
    │   ├── IPathFinder.java
    │   └── PathFinderType.java
    ├── simulation
    │   ├── ArrivalBehavior.java
    │   └── SimulationEngine.java
    └── view
        ├── CreateDialogs.java
        ├── GraphView.java
        ├── HelpDialog.java
        ├── LegendPanel.java
        ├── MainApp.java
        ├── PatientPanel.java
        ├── SimulationBar.java
        ├── StatsPanel.java
        └── ToolBox.java
```

---

## Javadoc

La documentation générée est disponible dans le dossier `doc/`. Ouvrir `doc/index.html` dans un navigateur.
