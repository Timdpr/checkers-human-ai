package main.java;

import javafx.application.Application;
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
        Parent root = FXMLLoader.load(getClass().getResource("/main/res/view.fxml"));
        primaryStage.setTitle("Minimax Checkers");

        primaryStage.getIcons().addAll(
                // JavaFX is bad at auto choosing icons, so, many are given:
                new Image(getClass().getResourceAsStream("/main/res/icon_16.png")),
                new Image(getClass().getResourceAsStream("/main/res/icon_32.png")),
                new Image(getClass().getResourceAsStream("/main/res/icon_48.png")),
                new Image(getClass().getResourceAsStream("/main/res/icon_64.png")),
                new Image(getClass().getResourceAsStream("/main/res/icon_128.png")),
                new Image(getClass().getResourceAsStream("/main/res/icon_256.png")),
                new Image(getClass().getResourceAsStream("/main/res/icon_512.png"))
        );

        primaryStage.setScene(new Scene(root, 800, 480));
        primaryStage.setMinHeight(480 + 22);
        primaryStage.setMaxHeight(480 + 22);
        primaryStage.setMinWidth(700);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
