package fr.projet;

import fr.projet.ui.MainApp;

/**
 * Application entry point.
 * Delegates immediately to the JavaFX launcher.
 * <p>
 * NOTE: Main must NOT extend Application — this is the recommended pattern
 * for Java 11+ modular projects so the JavaFX toolkit initialises correctly.
 * </p>
 */
public class Main {

    public static void main(String[] args) {
        MainApp.launch(MainApp.class, args);
    }
}
