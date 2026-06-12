/**
 * 
 */
/**
 * 
 */
module lifeline {
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