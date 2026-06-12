package view;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

// import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.Polygon;

import javafx.scene.text.Text;
import model.*;
import model.agent.Agent;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Node;
import model.graph.NodeType;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
import javafx.geometry.Point2D;
import java.util.function.Consumer;

import java.util.*;

public class GraphView extends Pane {
	
	/** Constants used to define the rendering limits of the graph */
	private static final double NODE_RADIUS = 25.0;
	private static final double VIEW_MARGIN = NODE_RADIUS + 5.0;

    /** Currently selected node for visual highlighting */
    private Node selectedNode;

    /** Currently selected edge for visual highlighting */
    private Edge selectedEdge;

    /** Callback invoked when a node is clicked */
    private Consumer<Node> nodeClickHandler = node -> {};

    private final Map<Node, Circle> nodeViews = new HashMap<>();
    private final Map<Agent, Circle> agentViews = new HashMap<>();
    private final Map<Edge, Line> edgeViews = new HashMap<>();

    /** Coordinates of the user's click on a point in the interface */
    private Consumer<Point2D> backgroundClickHandler = point -> {};

    /** Agents currently displayed on the graph*/
    private List<Agent> currentAgents = Collections.emptyList();

    /** Graph currently rendered by the view */
    private Graph graph;

    /** Stores how many agents are displayed at the same visual position */
    private final Map<String, Integer> positionCounts = new HashMap<>();

    /** Callback invoked when an edge is clicked */
    private Consumer<Edge> edgeClickHandler = edge -> {};
    
    private Agent selectedAgent;
    private Consumer<Agent> agentClickHandler = agent -> {};

    private Node draggedNode;
    private boolean nodeWasDragged;
    private double dragOffsetX;
    private double dragOffsetY;
    
    /**Handler to display the node's new coordinates after dragging it */
    private Consumer<Node> nodeDragHandler = node -> {};


    /**
     * Creates the view and prepares background click handling.
     */
    public GraphView() {
        setPrefSize(800, 600);
        setPickOnBounds(true);

        addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getTarget() == this) {
            	backgroundClickHandler.accept(clampPoint(new Point2D(e.getX(), e.getY())));
            }
        });
    }

    /**
     * Sets the handler used when the user clicks on empty space.
     *
     * @param handler background click handler
     */
    public void setBackgroundClickHandler(Consumer<Point2D> handler) {
        this.backgroundClickHandler = (handler != null) ? handler : point -> {};
    }
    
    /**
     * 
     * @param handler
     */
    public void setNodeDragHandler(Consumer<Node> handler) {
        this.nodeDragHandler = (handler != null) ? handler : node -> {};
    }

    /**
     * Renders the graph by drawing nodes, edges, and labels.
     *
     * @param graph the graph to render
     */
    public void renderGraph(Graph graph) {
        this.graph = graph;

        getChildren().clear();
        nodeViews.clear();
        edgeViews.clear();

        List<Node> nodes = new ArrayList<>(graph.getAllNodes());
        int n = nodes.size();

        double centerX = 400;
        double centerY = 300;
        double radius = 200;

        // =========================
        // NODES
        // =========================
        
        Map<Node, Text> labels = new HashMap<>();
        for (int i = 0; i < n; i++) {

            Node node = nodes.get(i);

            double x;
            double y;

            if (node.getX() != null && node.getY() != null) {
                x = node.getX();
                y = node.getY();
            } else {
                double angle = 2 * Math.PI * i / n;

                x = centerX + radius * Math.cos(angle);
                y = centerY + radius * Math.sin(angle);

                node.setX(x);
                node.setY(y);
            }


            Text label = new Text(String.valueOf(node.getId()));
            label.setMouseTransparent(true);
            label.setX(x - 5);
            label.setY(y + 5);
            label.setFill(Color.BLACK);

            Circle circle = new Circle(25);
            circle.setCenterX(x);
            circle.setCenterY(y);
            circle.setCursor(Cursor.HAND);

            circle.setOnMousePressed(e -> {
                draggedNode = node;
                nodeWasDragged = false;
                Point2D p = sceneToLocal(e.getSceneX(), e.getSceneY());
                dragOffsetX = node.getX() - p.getX();
                dragOffsetY = node.getY() - p.getY();
                e.consume();
            });

            circle.setOnMouseDragged(e -> {
                if (draggedNode != node) return;
                nodeWasDragged = true;
                Point2D p = sceneToLocal(e.getSceneX(), e.getSceneY());
                
                double newX = clampX(p.getX() + dragOffsetX);
                double newY = clampY(p.getY() + dragOffsetY);

                node.setX(newX);
                node.setY(newY);
                
                nodeDragHandler.accept(node);
                circle.setCenterX(newX);
                circle.setCenterY(newY);
                label.setX(newX - 5);
                label.setY(newY + 5);
                e.consume();
            });

            circle.setOnMouseReleased(e -> {
                if (draggedNode == node) {
                    draggedNode = null;
                    if (nodeWasDragged) {
                        renderGraph(graph);
                    } else {
                        nodeClickHandler.accept(node);
                    }
                    nodeWasDragged = false;
                }

                e.consume();
            });

            nodeViews.put(node, circle);

            if (node.equals(selectedNode)) {
                circle.setFill(Color.GOLD);
                circle.setStroke(Color.DODGERBLUE);
                circle.setStrokeWidth(3);
            } else {
            	circle.setFill(getNodeColor(node));
                circle.setStroke(Color.BLACK);
                circle.setStrokeWidth(1);
            }




            nodeViews.put(node, circle);
            labels.put(node, label);
        }

        // =========================
        // EDGES
        // =========================

        for (Node node : nodes) {
            for (Edge edge : graph.getEdges(node)) {



                Circle src = nodeViews.get(edge.getSource());
                Circle dst = nodeViews.get(edge.getDestination());

                if (src == null || dst == null) {
                	continue;
                }
                
                // Calculation to display two edges in the case of an undirected edge
                double x1 = src.getCenterX();
                double y1 = src.getCenterY();
                double x2 = dst.getCenterX();
                double y2 = dst.getCenterY();

                double dx = x2 - x1;
                double dy = y2 - y1;
                double length = Math.sqrt(dx * dx + dy * dy);

                double offsetX = 0;
                double offsetY = 0;

                // Appliquer un offset si l'arête est non orientée OU s'il existe une arête dans le sens inverse
                boolean hasReverseEdge = graph.hasEdge(edge.getDestination(), edge.getSource());

                if ((!edge.isOriented() || hasReverseEdge) && length > 0) {
                    double offset = 8.0;
                    offsetX = -dy / length * offset;
                    offsetY = dx / length * offset;
                }
                
                Line line = new Line(
                    x1 + offsetX,
                    y1 + offsetY,
                    x2 + offsetX,
                    y2 + offsetY
                );

                applyEdgeStyle(line, edge);
                line.setCursor(Cursor.HAND);

                // Highlight selected edge
                if (edge.equals(selectedEdge)) {
                    line.setStroke(Color.GOLD);
                    line.setStrokeWidth(line.getStrokeWidth() + 2);
                } else {
                    applyEdgeStyle(line, edge);
                }

                final Edge currentEdge = edge;
                line.setOnMouseClicked(e -> {
                    edgeClickHandler.accept(currentEdge);
                    e.consume();
                });

                // Edge hover effect
                line.setOnMouseEntered(e -> {
                    if (!currentEdge.equals(selectedEdge)) {
                        line.setStroke(Color.RED);
                    }
                });

                line.setOnMouseExited(e -> {
                    if (currentEdge.equals(selectedEdge)) {
                        applyEdgeStyle(line, currentEdge);
                        line.setStroke(Color.GOLD);
                        line.setStrokeWidth(line.getStrokeWidth() + 2);
                    } else {
                        applyEdgeStyle(line, currentEdge);
                    }
                });

                Text weight = new Text(String.valueOf(edge.getDistance()));
                weight.setFill(Color.BLUE);
                weight.setFont(javafx.scene.text.Font.font(22));
                weight.setMouseTransparent(true);

                double midX = (src.getCenterX() + dst.getCenterX()) / 2;
                double midY = (src.getCenterY() + dst.getCenterY()) / 2;

                weight.setX(midX);
                weight.setY(midY);
                
                // Arrows
                Polygon arrowToDst = createArrowHead(
                        x1 + offsetX,
                        y1 + offsetY,
                        x2 + offsetX,
                        y2 + offsetY
                );

                edgeViews.put(edge, line);
                
                getChildren().add(line);
                getChildren().add(weight);
                getChildren().add(arrowToDst);
            }
        }
        
        for (Node node : nodes) {

            Circle circle = nodeViews.get(node);
            Text label = labels.get(node);

            // First, draw the node circle
            if (circle != null) {
                getChildren().add(circle);
            }

            // Draw a red cross over the node if it is blocked
            addBlockedCross(node);
            
            // Draw the node id above the circle and the cross
            if (label != null) {
                getChildren().add(label);
            }
        }

     // Forcer la recréation des cercles agents après un renderGraph
        agentViews.values().forEach(getChildren()::remove);
        agentViews.clear();
        redrawAgents();
    }

    /**
     * @param agents
     */
    public void renderAgents(List<Agent> agents) {
        currentAgents = (agents != null) ? new ArrayList<>(agents) : Collections.emptyList();
        redrawAgents();
    }

    // =====================================================
    // EDITING THE GRAPH ON THE INTERFACE
    // =====================================================

    /**
     * Sets the callback called when a node is clicked.
     *
     * @param handler node click handler
     */
    public void setNodeClickHandler(Consumer<Node> handler) {
        this.nodeClickHandler = (handler != null) ? handler : node -> {};
    }

    /**
     * Sets the callback called when an edge is clicked.
     *
     * @param handler edge click handler
     */
    public void setEdgeClickHandler(Consumer<Edge> handler) {
        this.edgeClickHandler = (handler != null) ? handler : edge -> {};
    }

    /**
     * Updates the selected node and refreshes the visual highlight.
     *
     * @param node selected node
     */
    public void setSelectedNode(Node node) {
        this.selectedNode = node;
        this.selectedEdge = null;
        refreshSelection();
        redrawAgents();
    }

    /**
     * Updates the selected edge and refreshes the visual highlight.
     *
     * @param edge selected edge
     */
    public void setSelectedEdge(Edge edge) {
        this.selectedEdge = edge;
        this.selectedNode = null;
        refreshSelection();
    }
    
    
    /**
     * Sets the callback invoked when an agent is clicked on the graph.
     *
     * @param handler consumer receiving the clicked agent
     */
    public void setAgentClickHandler(Consumer<Agent> handler) {
        this.agentClickHandler = (handler != null) ? handler : agent -> {};
    }

    /**
     * Highlights the selected agent with a red stroke.
     * Clears any previously selected agent highlight.
     *
     * @param agent the agent to highlight, or null to clear selection
     */
    public void setSelectedAgent(Agent agent) {
        this.selectedAgent = agent;
        this.selectedNode = null;
        this.selectedEdge = null;
        refreshSelection();
        redrawAgents();
    }

    /**
     * Clears the current selection (node or edge).
     */
    public void clearSelection() {
    	this.selectedAgent = null;
        this.selectedNode = null;
        this.selectedEdge = null;
        refreshSelection();
        redrawAgents();

    }

    /**
     * Refreshes the appearance of all nodes and edges according to the current selection.
     */
    private void refreshSelection() {
        for (Map.Entry<Node, Circle> entry : nodeViews.entrySet()) {
            Node node = entry.getKey();
            Circle circle = entry.getValue();

            if (node.equals(selectedNode)) {
                circle.setFill(Color.GOLD);
                circle.setStroke(Color.DODGERBLUE);
                circle.setStrokeWidth(3);
            } else {
            	circle.setFill(getNodeColor(node));
                circle.setStroke(Color.BLACK);
                circle.setStrokeWidth(1);
            }
        }

        for (Map.Entry<Edge, Line> entry : edgeViews.entrySet()) {
            Edge edge = entry.getKey();
            Line line = entry.getValue();

            if (edge.equals(selectedEdge)) {
                applyEdgeStyle(line, edge);
                line.setStroke(Color.GOLD);
                line.setStrokeWidth(line.getStrokeWidth() + 2);
            } else {
                applyEdgeStyle(line, edge);
            }
        }
    }

    /**
     * Redraws agents using their current progress on edges.
     */
    private void redrawAgents() {
        positionCounts.clear();

        // Supprimer uniquement les cercles des agents qui ne sont plus dans la liste
        Set<Agent> currentSet = new HashSet<>(currentAgents);
        List<Agent> toRemove = new ArrayList<>();
        for (Agent agent : agentViews.keySet()) {
            if (!currentSet.contains(agent)) {
                toRemove.add(agent);
            }
        }
        for (Agent agent : toRemove) {
            getChildren().remove(agentViews.get(agent));
            agentViews.remove(agent);
        }

        for (Agent agent : currentAgents) {

            // Réutiliser le cercle existant ou en créer un nouveau
            Circle agentCircle = agentViews.get(agent);
            if (agentCircle == null) {
                agentCircle = new Circle(10);
                agentCircle.setMouseTransparent(false);
                final Agent currentAgent = agent;
                agentCircle.setOnMouseClicked(e -> {
                    agentClickHandler.accept(currentAgent);
                    e.consume();
                });
                agentViews.put(agent, agentCircle);
                getChildren().add(agentCircle);
            }

            // Mettre à jour la couleur et le contour
            agentCircle.setFill(getAgentColor(agent));
            agentCircle.setStroke(agent.equals(selectedAgent) ? Color.RED : Color.BLACK);
            agentCircle.setStrokeWidth(agent.equals(selectedAgent) ? 3 : 1);

            // Calculer la position
            double x;
            double y;

            Circle currentNodeCircle = nodeViews.get(agent.getCurrentPosition());

            if (currentNodeCircle == null) {
                continue;
            }

            if (graph != null && agent.getNextNode() != null
                    && !agent.getCurrentPosition().equals(agent.getNextNode())) {

                Edge currentEdge = null;
                for (Edge edge : graph.getEdges(agent.getCurrentPosition())) {
                    if (edge.getDestination().equals(agent.getNextNode())) {
                        currentEdge = edge;
                        break;
                    }
                }

                if (currentEdge != null) {
                    Circle nextNodeCircle = nodeViews.get(agent.getNextNode());
                    if (nextNodeCircle != null) {
                        double effectiveDist = agent.getCurrentEffectiveDistance() > 0
                                ? agent.getCurrentEffectiveDistance()
                                : currentEdge.getDistance();
                        double ratio = agent.getProgressOnEdge() / effectiveDist;
                        ratio = Math.max(0.0, Math.min(ratio, 1.0));

                        x = currentNodeCircle.getCenterX()
                                + (nextNodeCircle.getCenterX() - currentNodeCircle.getCenterX()) * ratio;
                        y = currentNodeCircle.getCenterY()
                                + (nextNodeCircle.getCenterY() - currentNodeCircle.getCenterY()) * ratio;
                    } else {
                        x = currentNodeCircle.getCenterX();
                        y = currentNodeCircle.getCenterY();
                    }
                } else {
                    x = currentNodeCircle.getCenterX();
                    y = currentNodeCircle.getCenterY();
                }
            } else {
                x = currentNodeCircle.getCenterX();
                y = currentNodeCircle.getCenterY();
            }

            // Offset si plusieurs agents au même endroit
            String key = Math.round(x) + ":" + Math.round(y);
            int index = positionCounts.getOrDefault(key, 0);
            positionCounts.put(key, index + 1);

            double offset = 8.0;
            double angle = index * (Math.PI / 3);
            x += Math.cos(angle) * offset;
            y += Math.sin(angle) * offset;

            agentCircle.setCenterX(x);
            agentCircle.setCenterY(y);
            agentCircle.toFront();
        }
    }

    /**
     * Displays each edge type with a different design to understand better the interface.
     * 
     * @param line edge modeling
     * @param edge considered edge
     */
    private void applyEdgeStyle(Line line, Edge edge) {
        line.setStroke(Color.BLACK);
        line.getStrokeDashArray().clear();
        
        switch (edge.getType()) {
        	case HIGHWAY:
        		line.setStrokeWidth(5);
        		break;
        	case DIRT_ROAD:
                line.setStrokeWidth(3);
                line.getStrokeDashArray().addAll(10.0, 7.0);
                break;
        	default:
        		line.setStrokeWidth(2);
        }
    }

    /**
     * Returns a stable color for an agent based on its id.
     *
     * @param agent agent to color
     * @return display color
     */
    private Color getAgentColor(Agent agent) {
        return Color.hsb((agent.getId() * 47) % 360, 0.85, 0.95);
    }

    /**
     * Sets the cursor used while the node creation mode is active.
     *
     * @param active true to display the crosshair cursor, false to restore the default cursor
     */
    public void setNodeCreationMode(boolean active) {
        setCursor(active ? Cursor.CROSSHAIR : Cursor.DEFAULT);
    }

    /**
     * Sets the cursor used while an editing mode is active.
     *
     * @param active true to display the crosshair cursor, false to restore the default cursor
     */
    public void setEdgeCreationMode(boolean active) {
        setCursor(active ? Cursor.CROSSHAIR : Cursor.DEFAULT);
    }

    /**
     * Sets the cursor used while deletion mode is active.
     *
     * @param active true to display the crosshair cursor, false to restore the default cursor
     */
    public void setDeleteMode(boolean active) {
        setCursor(active ? Cursor.CROSSHAIR : Cursor.DEFAULT);
    }

    
    /**
     * Creates a small arrowhead polygon at the end of an edge.
     *
     * @param x1 start x
     * @param y1 start y
     * @param x2 end x
     * @param y2 end y
     * @return arrowhead polygon
     */
    private Polygon createArrowHead(double x1, double y1, double x2, double y2) {
        double size = 10.0;
        double nodeRadius = 25.0; // Match the node circle radius
        double gap = 4.0;

        double angle = Math.atan2(y2 - y1, x2 - x1);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        double endX = x2 - (nodeRadius + gap) * cos;
        double endY = y2 - (nodeRadius + gap) * sin;

        double xA = endX - size * cos + (size / 2.0) * sin;
        double yA = endY - size * sin - (size / 2.0) * cos;

        double xB = endX - size * cos - (size / 2.0) * sin;
        double yB = endY - size * sin + (size / 2.0) * cos;

        Polygon arrow = new Polygon(
                endX, endY,
                xA, yA,
                xB, yB
        );

        arrow.setFill(Color.BLACK);
        return arrow;
    }
    
    /**
     * Assign a color to a node based on its type.
     * 
     * @param node
     * @return
     */
    private Color getNodeColor(Node node) {
        if (node.getType() == NodeType.HOSPITAL) {
            return Color.DODGERBLUE;
        }

        if (node.getType() == NodeType.ACCIDENT) {
            return Color.CRIMSON;
        }

        return Color.LIGHTGRAY;
    }
    
    /**
     * Displays a cross on a node to indicate to the user that the node is locked.
     * 
     * @param node to block
     */
    private void addBlockedCross(Node node) {
        if (!node.isBlocked()) {
            return;
        }

        Circle circle = nodeViews.get(node);
        if (circle == null) {
            return;
        }

        double x = circle.getCenterX();
        double y = circle.getCenterY();
        double size = 15;

        Line line1 = new Line(x - size, y - size, x + size, y + size);
        Line line2 = new Line(x + size, y - size, x - size, y + size);

        line1.setStroke(Color.BLACK);
        line2.setStroke(Color.BLACK);

        line1.setStrokeWidth(4);
        line2.setStrokeWidth(4);

        line1.setMouseTransparent(true);
        line2.setMouseTransparent(true);

        getChildren().addAll(line1, line2);
    }
    
    /**
     * Set a limit on the horizontal axis to prevent the graph from extending beyond the ToolBox.
     * 
     * @param x x-axis
     * @return corrected x-coordinate
     */
    private double clampX(double x) {
        double maxX = getWidth() - VIEW_MARGIN;

        if (maxX <= VIEW_MARGIN) {
            return x;
        }

        // x must be greater than VIEW_MARGIN but less than maxX
        return Math.max(VIEW_MARGIN, Math.min(x, maxX));
    }

    /**
     * Set a limit on the vertical axis to prevent the graph from extending beyond the ToolBox.
     * 
     * @param y y-axis
     * @return corrected y-coordinate
     */
    private double clampY(double y) {
        double maxY = getHeight() - VIEW_MARGIN;

        if (maxY <= VIEW_MARGIN) {
            return y;
        }

        return Math.max(VIEW_MARGIN, Math.min(y, maxY));
    }

    /**
     * Limits a complete point, meaning both x and y at the same time.
     * 
     * @param point
     * @return a new corrected point
     */
    private Point2D clampPoint(Point2D point) {
        return new Point2D(clampX(point.getX()), clampY(point.getY())
        );
    }
    
    public void setGraph(Graph graph) {
        this.graph = graph;
    }

}