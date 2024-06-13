package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PuzzleApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/opening.fxml"));
        stage.setTitle("Tower Puzzle Game");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
