package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Renderer extends Application{
    protected static int gateSize = 40;

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Renderer.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 800);
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.setTitle("Logit Gates");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
