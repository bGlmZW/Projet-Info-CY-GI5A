package fr.projet.ui;

import fr.projet.model.*;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.List;

/**
 * Stateless renderer: draws the full simulation frame onto a JavaFX
 * {@link GraphicsContext} every animation tick.
 *
 * <h3>Drawing order</h3>
 * <ol>
 *   <li>Background</li>
 *   <li>Edges (lines, coloured by congestion load)</li>
 *   <li>Nodes (circles, coloured by type and state)</li>
 *   <li>Agents (small dots, interpolated between node centres)</li>
 * </ol>
 */
public class GraphRenderer {

    // ── sizing constants ──────────────────────────────────────────────────────
    private static final double NODE_R  = 19;   // node circle radius
    private static final double AGENT_R =  7;   // agent dot radius

    // ── colours ───────────────────────────────────────────────────────────────
    private static final Color BG          = Color.web("#f0f3f4");
    private static final Color EDGE_NORMAL = Color.web("#bdc3c7");
    private static final Color EDGE_BUSY   = Color.web("#e67e22");
    private static final Color EDGE_FULL   = Color.web("#e74c3c");

    // node fill by type (normal state)
    private static final Color COL_HOSPITAL     = Color.web("#e74c3c");
    private static final Color COL_STATION      = Color.web("#27ae60");
    private static final Color COL_INTERSECTION = Color.web("#3498db");

    // node fill overrides by state
    private static final Color COL_CONGESTED  = Color.web("#e67e22");
    private static final Color COL_STRONG     = Color.web("#c0392b");
    private static final Color COL_BLOCKED    = Color.web("#2c3e50");

    // agent fill by behaviour
    private static final Color COL_CALM     = Color.web("#8e44ad");
    private static final Color COL_PRIORITY = Color.web("#e74c3c");
    private static final Color COL_YIELDING = Color.web("#f39c12");

    // =========================================================================
    // Public API
    // =========================================================================

    /**
     * Renders one complete frame.
     *
     * @param gc     JavaFX graphics context
     * @param graph  the road network
     * @param agents live agent list
     * @param w      canvas width
     * @param h      canvas height
     */
    public static void render(GraphicsContext gc,
                              Graph graph,
                              List<Agent> agents,
                              double w, double h) {
        drawBackground(gc, w, h);

        for (Edge e : graph.getEdges())  drawEdge(gc, e);
        for (Node n : graph.getNodes())  drawNode(gc, n);
        for (Agent a : agents)           drawAgent(gc, a);
    }

    // =========================================================================
    // Background
    // =========================================================================

    private static void drawBackground(GraphicsContext gc, double w, double h) {
        gc.setFill(BG);
        gc.fillRect(0, 0, w, h);

        // Subtle grid
        gc.setStroke(Color.web("#dee2e6"));
        gc.setLineWidth(0.5);
        for (double x = 40; x < w; x += 40) gc.strokeLine(x, 0, x, h);
        for (double y = 40; y < h; y += 40) gc.strokeLine(0, y, w, y);
    }

    // =========================================================================
    // Edges
    // =========================================================================

    private static void drawEdge(GraphicsContext gc, Edge edge) {
        double x1 = edge.getSource().getX();
        double y1 = edge.getSource().getY();
        double x2 = edge.getDestination().getX();
        double y2 = edge.getDestination().getY();

        // Colour by load ratio
        double load = edge.getLoadRatio();
        Color color;
        if      (load >= 1.0) color = EDGE_FULL;
        else if (load >= 0.5) color = EDGE_BUSY;
        else                  color = EDGE_NORMAL;

        gc.setStroke(color);
        gc.setLineWidth(load >= 0.5 ? 3.5 : 2.5);
        gc.strokeLine(x1, y1, x2, y2);

        // Arrow for one-way edges
        if (edge.isOriented()) {
            drawArrowHead(gc, x1, y1, x2, y2, color);
        }
    }

    /**
     * Draws a small arrow near the destination end of an oriented edge.
     */
    private static void drawArrowHead(GraphicsContext gc,
                                      double x1, double y1,
                                      double x2, double y2,
                                      Color color) {
        double angle  = Math.atan2(y2 - y1, x2 - x1);
        double spread = Math.toRadians(28);
        double len    = 13;

        // Place tip just outside the destination node circle
        double tx = x2 - NODE_R * Math.cos(angle);
        double ty = y2 - NODE_R * Math.sin(angle);

        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokeLine(tx, ty, tx - len * Math.cos(angle - spread),
                               ty - len * Math.sin(angle - spread));
        gc.strokeLine(tx, ty, tx - len * Math.cos(angle + spread),
                               ty - len * Math.sin(angle + spread));
    }

    // =========================================================================
    // Nodes
    // =========================================================================

    private static void drawNode(GraphicsContext gc, Node node) {
        double x = node.getX();
        double y = node.getY();

        Color fill   = nodeFill(node);
        Color border = fill.darker().darker();

        // Drop shadow
        gc.setFill(Color.color(0, 0, 0, 0.15));
        gc.fillOval(x - NODE_R + 3, y - NODE_R + 3, NODE_R * 2, NODE_R * 2);

        // Body
        gc.setFill(fill);
        gc.fillOval(x - NODE_R, y - NODE_R, NODE_R * 2, NODE_R * 2);

        // Border ring
        gc.setStroke(border);
        gc.setLineWidth(2);
        gc.strokeOval(x - NODE_R, y - NODE_R, NODE_R * 2, NODE_R * 2);

        // Icon letter centred inside circle
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(nodeIcon(node), x, y + 5);

        // Label below
        gc.setFill(Color.web("#2c3e50"));
        gc.setFont(Font.font("Arial", 10));
        gc.fillText(node.getLabel(), x, y + NODE_R + 13);

        // Capacity indicator (small text inside for hospitals/stations)
        if (node.getType() != NodeType.INTERSECTION) {
            gc.setFill(Color.color(1, 1, 1, 0.75));
            gc.setFont(Font.font("Arial", 8));
            gc.fillText(node.getCurrentLoad() + "/" + node.getMaxCapacity(),
                        x, y + NODE_R + 24);
        }
    }

    private static Color nodeFill(Node node) {
        switch (node.getState()) {
            case BLOCKED:           return COL_BLOCKED;
            case STRONG_CONGESTION: return COL_STRONG;
            case CONGESTED:         return COL_CONGESTED;
            default: break;
        }
        switch (node.getType()) {
            case HOSPITAL: return COL_HOSPITAL;
            case STATION:  return COL_STATION;
            default:       return COL_INTERSECTION;
        }
    }

    private static String nodeIcon(Node node) {
        switch (node.getType()) {
            case HOSPITAL: return "H";
            case STATION:  return "S";
            default:       return "·";
        }
    }

    // =========================================================================
    // Agents
    // =========================================================================

    private static void drawAgent(GraphicsContext gc, Agent agent) {
        double x = agent.getVisualX();
        double y = agent.getVisualY();

        Color fill = agentFill(agent);

        // Drop shadow
        gc.setFill(Color.color(0, 0, 0, 0.25));
        gc.fillOval(x - AGENT_R + 2, y - AGENT_R + 2, AGENT_R * 2, AGENT_R * 2);

        // Body
        gc.setFill(fill);
        gc.fillOval(x - AGENT_R, y - AGENT_R, AGENT_R * 2, AGENT_R * 2);

        // White ring
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1.5);
        gc.strokeOval(x - AGENT_R, y - AGENT_R, AGENT_R * 2, AGENT_R * 2);

        // ID number
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 8));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf(agent.getId()), x, y + 3);
    }

    private static Color agentFill(Agent agent) {
        switch (agent.getBehavior()) {
            case PRIORITY: return COL_PRIORITY;
            case YIELDING: return COL_YIELDING;
            default:       return COL_CALM;
        }
    }
}
