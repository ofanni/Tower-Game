package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class OpeningController {

    @FXML
    private TextField playerNameTextField;

    @FXML
    private void initialize() {

    }

    @FXML
    private void onStartGame(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/towerGame.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        if (playerNameTextField.getText().equals(" ") || playerNameTextField.getText().equals("")) {
            stage.setUserData("Player");
        } else {
            stage.setUserData(playerNameTextField.getText());
        }
        stage.setScene(new Scene(root));
        stage.show();
    }
}
