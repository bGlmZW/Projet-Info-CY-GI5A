/**
 * LifeLine GPS Ambulance Simulation.
 */
module ProjetGenieLogiciel {
    requires javafx.controls;
    requires javafx.graphics;

    exports fr.projet.ui;
    opens   fr.projet.ui to javafx.graphics, javafx.controls;
}
