package main.java;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author tp275
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/main/res/sample.fxml"));
        primaryStage.setTitle("Minimax Checkers");

        primaryStage.getIcons().addAll(
                // JavaFX is bad at auto choosing icons, so, many are given:
                new Image(getClass().getResourceAsStream("/main/res/icon_16.png")),
                new Image(getClass().getResourceAsStream("/main/res/icon_32.png")),
                new Image(getClass().getResourceAsStream("/main/res/icon_48.png")),
                new Image(getClass().getResourceAsStream("/main/res/icon_64.png")),
                new Image(getClass().getResourceAsStream("/main/res/icon_128.png")));

        primaryStage.setScene(new Scene(root, 800, 480));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
