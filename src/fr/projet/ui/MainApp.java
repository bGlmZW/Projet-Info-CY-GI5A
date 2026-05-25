package fr.projet.ui;

import fr.projet.model.*;
import fr.projet.simulation.SimulationEngine;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Main JavaFX application window for the LifeLine simulation.
 *
 * <h3>Layout</h3>
 * <pre>
 * ┌──────────────────────────────────────────────┐
 * │  🚑  LifeLine Simulation          (top bar)  │
 * ├───────────────────────────┬──────────────────┤
 * │                           │  LEGEND          │
 * │   Canvas (graph + agents) │  H  Hospital     │
 * │                           │  S  Station      │
 * │                           │  ·  Intersection │
 * ├───────────────────────────┴──────────────────┤
 * │ [▶ Play] [⏸ Pause] [↺ Reset]  Speed:[====]  │
 * │ Tick: 0   Agents: 5/5                        │
 * └──────────────────────────────────────────────┘
 * </pre>
 */
public class MainApp extends Application {

    // ── canvas dimensions ────────────────────────────────────────────────────
    private static final double CANVAS_W = 780;
    private static final double CANVAS_H = 520;

    // ── simulation state ─────────────────────────────────────────────────────
    private SimulationEngine engine;
    private boolean paused = true;

    // ── UI nodes ─────────────────────────────────────────────────────────────
    private Canvas canvas;
    private Label tickLabel;
    private Label agentLabel;
    private Label statusLabel;

    // ── animation timing ─────────────────────────────────────────────────────
    private long lastTickNano = 0;

    // =========================================================================
    // JavaFX entry point
    // =========================================================================

    @Override
    public void start(Stage stage) {

        engine = buildDemoScenario();

        // ── canvas ────────────────────────────────────────────────────────────
        canvas = new Canvas(CANVAS_W, CANVAS_H);
        renderFrame();

        // ── top bar ───────────────────────────────────────────────────────────
        Label title = new Label("🚑  LifeLine — GPS Ambulance Simulation");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        title.setStyle("-fx-text-fill: #2c3e50;");

        HBox topBar = new HBox(title);
        topBar.setPadding(new Insets(10, 18, 10, 18));
        topBar.setStyle("-fx-background-color: #dfe6e9;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        // ── legend panel (right) ──────────────────────────────────────────────
        VBox legend = buildLegend();
        legend.setPrefWidth(160);

        // ── centre area (canvas + legend side by side) ────────────────────────
        HBox centre = new HBox(canvas, legend);
        centre.setStyle("-fx-background-color: #f0f3f4;");

        // ── control bar (bottom) ──────────────────────────────────────────────
        HBox controls = buildControls();

        // ── root layout ───────────────────────────────────────────────────────
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(centre);
        root.setBottom(controls);

        Scene scene = new Scene(root);
        stage.setTitle("LifeLine");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // ── animation loop ────────────────────────────────────────────────────
        Slider speedSlider = (Slider) controls.lookup("#speedSlider");
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long nowNano) {
                if (!paused) {
                    double ticksPerSecond = speedSlider.getValue();
                    long intervalNano = (long) (1_000_000_000.0 / ticksPerSecond);
                    if (nowNano - lastTickNano >= intervalNano) {
                        engine.tick();
                        lastTickNano = nowNano;
                    }
                }
                renderFrame();
                tickLabel.setText("Tick:  " + engine.getTickCount());
                agentLabel.setText("Agents: " + engine.getAgents().size());
            }
        };
        timer.start();
    }

    // =========================================================================
    // UI builders
    // =========================================================================

    /**
     * Builds the bottom control bar.
     */
    private HBox buildControls() {

        // Buttons
        Button playBtn  = new Button("▶  Play");
        Button pauseBtn = new Button("⏸  Pause");
        Button resetBtn = new Button("↺  Reset");

        styleButton(playBtn,  "#27ae60");
        styleButton(pauseBtn, "#e67e22");
        styleButton(resetBtn, "#2980b9");

        playBtn.setOnAction(e -> {
            paused = false;
            statusLabel.setText("Running…");
            statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        });
        pauseBtn.setOnAction(e -> {
            paused = true;
            statusLabel.setText("Paused");
            statusLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
        });
        resetBtn.setOnAction(e -> {
            paused = true;
            engine = buildDemoScenario();
            renderFrame();
            statusLabel.setText("Reset — press Play");
            statusLabel.setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
        });

        // Speed slider
        Label speedLbl = new Label("Speed:");
        speedLbl.setFont(Font.font("Arial", 12));

        Slider speedSlider = new Slider(1, 30, 6);
        speedSlider.setId("speedSlider");
        speedSlider.setPrefWidth(130);
        speedSlider.setShowTickLabels(true);
        speedSlider.setMajorTickUnit(10);
        speedSlider.setSnapToTicks(false);

        // Stats labels
        tickLabel   = new Label("Tick:  0");
        agentLabel  = new Label("Agents: 5");
        statusLabel = new Label("Press Play to start");

        tickLabel.setFont(Font.font("Courier New", 12));
        agentLabel.setFont(Font.font("Courier New", 12));
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        statusLabel.setStyle("-fx-text-fill: #7f8c8d;");

        Separator sep1 = new Separator(javafx.geometry.Orientation.VERTICAL);
        Separator sep2 = new Separator(javafx.geometry.Orientation.VERTICAL);

        HBox bar = new HBox(10,
                playBtn, pauseBtn, resetBtn,
                sep1,
                speedLbl, speedSlider,
                sep2,
                tickLabel, agentLabel, statusLabel);
        bar.setPadding(new Insets(10, 16, 10, 16));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #dfe6e9;");
        return bar;
    }

    /**
     * Builds the right-side legend panel.
     */
    private VBox buildLegend() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(14, 12, 14, 12));
        box.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6;"
                   + "-fx-border-width: 0 0 0 1;");

        Label title = new Label("LEGEND");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        title.setStyle("-fx-text-fill: #7f8c8d;");

        box.getChildren().addAll(
            title,
            new Separator(),
            legendSection("NODES"),
            legendItem("H", "#e74c3c", "Hospital"),
            legendItem("S", "#27ae60", "Station"),
            legendItem("·", "#3498db", "Intersection"),
            new Label(""),
            legendSection("NODE STATE"),
            legendItem("■", "#e67e22", "Congested"),
            legendItem("■", "#e74c3c", "Strong cong."),
            legendItem("■", "#2c3e50", "Blocked"),
            new Label(""),
            legendSection("AGENTS"),
            legendItem("●", "#8e44ad", "Calm"),
            legendItem("●", "#e74c3c", "Priority"),
            legendItem("●", "#f39c12", "Yielding")
        );
        return box;
    }

    private Label legendSection(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        l.setStyle("-fx-text-fill: #95a5a6;");
        return l;
    }

    private HBox legendItem(String icon, String color, String label) {
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px;"
                       + "-fx-font-weight: bold; -fx-min-width: 18px;");
        Label textLbl = new Label(label);
        textLbl.setFont(Font.font("Arial", 11));
        HBox row = new HBox(6, iconLbl, textLbl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;"
                   + "-fx-font-weight: bold; -fx-font-size: 12px;"
                   + "-fx-background-radius: 5; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: derive(" + color + ", -15%); -fx-text-fill: white;"
              + "-fx-font-weight: bold; -fx-font-size: 12px;"
              + "-fx-background-radius: 5; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + color + "; -fx-text-fill: white;"
              + "-fx-font-weight: bold; -fx-font-size: 12px;"
              + "-fx-background-radius: 5; -fx-cursor: hand;"));
    }

    // =========================================================================
    // Rendering
    // =========================================================================

    private void renderFrame() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        GraphRenderer.render(gc, engine.getGraph(), engine.getAgents(),
                             CANVAS_W, CANVAS_H);
    }

    // =========================================================================
    // Demo scenario
    // =========================================================================

    /**
     * Builds a hard-coded city map:
     * 2 hospitals (H), 2 dispatch stations (S), 6 intersections (I).
     * 5 ambulance agents start from stations and head to hospitals.
     */
    private SimulationEngine buildDemoScenario() {
        // Reset ID counters so every Reset gives fresh IDs
        Node.resetIdCounter();
        Edge.resetIdCounter();
        Agent.resetIdCounter();

        Graph graph = new Graph();

        // ── nodes ────────────────────────────────────────────────────────────
        //   (label, x, y, maxCapacity, type)
        Node h1 = new Node("Hôpital Nord",  100, 90,  8, NodeType.HOSPITAL);
        Node h2 = new Node("Hôpital Sud",   680, 430, 8, NodeType.HOSPITAL);
        Node s1 = new Node("Station Ouest",  90, 430, 6, NodeType.STATION);
        Node s2 = new Node("Station Est",   690, 90,  6, NodeType.STATION);
        Node i1 = new Node("I1",            250, 170, 4, NodeType.INTERSECTION);
        Node i2 = new Node("I2",            530, 170, 4, NodeType.INTERSECTION);
        Node i3 = new Node("I3",            250, 350, 4, NodeType.INTERSECTION);
        Node i4 = new Node("I4",            530, 350, 4, NodeType.INTERSECTION);
        Node i5 = new Node("I5",            390, 260, 4, NodeType.INTERSECTION);
        Node i6 = new Node("I6",            620, 255, 4, NodeType.INTERSECTION);

        for (Node n : new Node[]{h1, h2, s1, s2, i1, i2, i3, i4, i5, i6}) {
            graph.addNode(n);
        }

        // ── edges (all bidirectional by default) ──────────────────────────────
        graph.addEdge(new Edge(h1, i1));
        graph.addEdge(new Edge(h1, i3));
        graph.addEdge(new Edge(s2, i2));
        graph.addEdge(new Edge(s1, i3));
        graph.addEdge(new Edge(i1, i2));
        graph.addEdge(new Edge(i1, i3));
        graph.addEdge(new Edge(i1, i5));
        graph.addEdge(new Edge(i2, i5));
        graph.addEdge(new Edge(i2, i6));
        graph.addEdge(new Edge(i3, i4));
        graph.addEdge(new Edge(i3, i5));
        graph.addEdge(new Edge(i4, i5));
        graph.addEdge(new Edge(i4, h2));
        graph.addEdge(new Edge(i5, i6));
        graph.addEdge(new Edge(i6, h2));

        // ── simulation engine ─────────────────────────────────────────────────
        SimulationEngine eng = new SimulationEngine(graph);

        // 5 ambulances: mix of behaviors and routes
        eng.addAgent(new Agent(0.14, s1, h2, AgentBehavior.CALM));
        eng.addAgent(new Agent(0.20, s1, h1, AgentBehavior.PRIORITY));
        eng.addAgent(new Agent(0.13, s2, h1, AgentBehavior.CALM));
        eng.addAgent(new Agent(0.17, s2, h2, AgentBehavior.YIELDING));
        eng.addAgent(new Agent(0.22, i5, h1, AgentBehavior.PRIORITY));

        return eng;
    }
}
