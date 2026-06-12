/**
 * LifeLine GPS Emergency Simulation module.
 */
module ProjetGenieLogicel3 {
    requires transitive javafx.controls;
    requires transitive javafx.graphics;

    exports main;
    exports model;
    exports pathfinding;
    exports simulation;
    exports ui;
    exports controller;
    exports view;
    exports io;
}