/**
 * LifeLine GPS Emergency Simulation module.
 */
module ProjetGenieLogicel3 {
    requires transitive javafx.controls;
    requires transitive javafx.graphics;

    exports main;
    exports model.accident;
    exports model.agent;
    exports model.graph;
    exports pathfinding;
    exports simulation;
    exports ui;
    exports controller;
    exports view;
    exports io;
}