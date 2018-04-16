package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("gui/sample.fxml"));
        primaryStage.setTitle("Minimax Checkers");
        primaryStage.setScene(new Scene(root, 800, 480));
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
