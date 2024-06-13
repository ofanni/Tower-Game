package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import json.GameResults;
import json.ResultManager;
import model.Colors;
import model.Position;
import model.TowerPuzzleModel;
import org.tinylog.Logger;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

public class PuzzleController {

    @FXML
    private VBox box;

    private String playerName;

    @FXML
    private GridPane grid;

    private TowerPuzzleModel model = new TowerPuzzleModel();
    private BoardGameMoveSelector selector = new BoardGameMoveSelector(model);

    @FXML
    private ImageView[] pieceViews;

    private boolean isCompleted;

    private int moveCount;


    @FXML
    public void initialize() {
        moveCount = 0;
        for (int i = 0; i < TowerPuzzleModel.ROW_SIZE; i++) {
            var rowConstraint = new RowConstraints(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            rowConstraint.setPrefHeight(45);
            grid.getRowConstraints().add(i, rowConstraint);
        }
        grid.setPrefHeight(TowerPuzzleModel.ROW_SIZE * 50);
        box.setPrefHeight(grid.getPrefHeight() + 100);
        pieceViews = Stream.of("kkorong.png", "pkorong.png", "rud.png")
                .map(s -> "/images/" + s)
                .map(Image::new)
                .map(ImageView::new)
                .toArray(ImageView[]::new);
        populateGrid();
        Platform.runLater(() -> {
            Stage stage = (Stage) grid.getScene().getWindow();
            playerName = stage.getUserData().toString();
        });
    }


    private void populateGrid() {
        grid.getChildren().removeAll(grid.getChildren());
        for (int row = 0; row < grid.getRowCount(); row++) {
            for (int col = 0; col < grid.getColumnCount(); col++) {
                final var square = new StackPane();
                var im = new ImageView();
                final int i = row;
                final int j = col;
                square.setStyle("-fx-background-color: white");
                im.imageProperty().bind(new ObjectBinding<Image>() {
                    {
                        super.bind(model.diskProperty(i, j));
                    }

                    @Override
                    protected Image computeValue() {
                        return switch (model.diskProperty(i, j).get().getColors()) {
                            case BLUE -> pieceViews[0].getImage();
                            case RED -> pieceViews[1].getImage();
                            case EMPTY -> pieceViews[2].getImage();
                        };
                    }
                });
                if (model.diskProperty(i, j).get().getColors() == Colors.EMPTY) {
                    im.setFitHeight(50);
                    im.setFitWidth(10);
                } else {
                    im.setFitHeight(40);
                    im.setFitWidth(model.diskProperty(i, j).get().getValue() * 20 + 20);
                }
                square.getChildren().add(im);
                square.setOnMouseClicked(mouseEvent -> {
                    try {
                        handleMouseClick(mouseEvent);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                grid.add(square, col, row);
            }
        }
    }

    private void handleMouseClick(MouseEvent mouseEvent) throws IOException {
        var square = (StackPane) mouseEvent.getSource();
        var row = GridPane.getRowIndex(square);
        var col = GridPane.getColumnIndex(square);
        if (selector.getPhase().equals(BoardGameMoveSelector.Phase.SELECT_FROM) && model.diskProperty(row, col).get().getColors() == Colors.EMPTY) {
            Logger.info("invalid");
            selector.reset();
        } else if (selector.getPhase() == BoardGameMoveSelector.Phase.SELECT_FROM && !model.getTopDisks().contains(new Position(row, col))) {
            selector.reset();
            Logger.info("invalid");
        } else if (selector.getPhase().equals(BoardGameMoveSelector.Phase.SELECT_FROM)) {
            selector.select(model.diskProperty(row, col).get().getPosition());
        } else if (selector.getPhase().equals(BoardGameMoveSelector.Phase.SELECT_TO) && !(model.getTopDisks().contains(new Position(row + 1, col)) || row == grid.getRowCount() - 1)) {
            Logger.info("invalid");
            selector.reset();
        } else if (selector.getPhase().equals(BoardGameMoveSelector.Phase.SELECT_TO) && model.diskProperty(row, col).get().getColors() == Colors.EMPTY) {

            selector.select(model.diskProperty(row, col).get().getPosition());
            if (selector.isInvalidSelection()) {
                selector.reset();
                Logger.info("invalid");
            } else {
                if (selector.isReadyToMove()) {
                    Logger.info("{} moved! {} => {}", playerName, selector.getFrom(), selector.getTo());
                    selector.makeMove();
                    populateGrid();
                    moveCount++;
                    if (model.isSolved()) {
                        solved();
                    }
                }
            }
        } else {
            selector.reset();
        }
        System.out.println();
    }

    @FXML
    private void onGiveUp() throws IOException {
        Logger.info("{} gave up", playerName);
        isCompleted = false;
        toNext();
    }

    private void solved() throws IOException {
        Logger.info("solved");
        isCompleted = true;
        toNext();
    }

    private void toNext() throws IOException {
        saveData();
        Logger.info("{} moved to the next window", playerName);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/end.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) grid).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void saveData() throws IOException {
        var outputData = new GameResults(playerName, moveCount, Boolean.toString(isCompleted));
        ResultManager.saveGameDataToJSON(outputData);
        Logger.info("Game data saved to JSON.");
    }


}
