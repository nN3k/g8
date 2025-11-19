package main;

import help.Log;
import javafx.application.Application;
import logicUtility.Gate;
import logicUtility.Wire;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Application.launch(Renderer.class, args);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    Log.logSave();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Thread.currentThread().interrupt();
            }
        });
    }
    public static void resetAll() {
        Gate.resetGate();
        Wire.resetWire();
    }
}
