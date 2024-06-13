package gui;

import game.console.Game;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import json.GameResults;
import json.ResultManager;
import org.tinylog.Logger;

import java.util.List;

public class EndController {

    @FXML
    private Label playerNameLabel;

    @FXML
    private Label moveCountLabel;

    @FXML
    private Label isCompletedLabel;

    @FXML
    private TableView<GameResults> table;


    @FXML
    private void initialize() {


        List<GameResults> resultList = ResultManager.readGameDataFromJSON("results.json");
        Logger.info("Game data loaded.");
        var last = resultList.getLast();
        playerNameLabel.textProperty().set(last.getPlayerName());
        moveCountLabel.textProperty().set(Integer.toString(last.getMoveCount()));
        isCompletedLabel.textProperty().set(last.getIsCompleted());
        ObservableList<GameResults> obsList = FXCollections.observableArrayList(resultList);
        table.setItems(obsList);

        TableColumn<GameResults, String> playerNameCol = new TableColumn<>("Name");
        playerNameCol.setCellValueFactory(new PropertyValueFactory<>("playerName"));

        TableColumn<GameResults, Integer> moveCol = new TableColumn<>("Moves");
        moveCol.setCellValueFactory(new PropertyValueFactory<>("moveCount"));

        TableColumn<GameResults, String> stateCol = new TableColumn<>("State");
        stateCol.setCellValueFactory(new PropertyValueFactory<>("isCompleted"));

        table.getColumns().add(playerNameCol);
        table.getColumns().add(moveCol);
        table.getColumns().add(stateCol);

    }

}
