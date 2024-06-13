package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.ObjectBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Colors;
import model.Position;
import model.TowerPuzzleModel;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.stream.Stream;

public class PuzzleController {


    @FXML
    private GridPane grid;

    private TowerPuzzleModel model = new TowerPuzzleModel();
    private BoardGameMoveSelector selector = new BoardGameMoveSelector(model);

    @FXML
    private ImageView[] pieceViews;


    @FXML
    public void initialize() {
        for (int i = 0; i < TowerPuzzleModel.ROW_SIZE; i++) {
            var rowConstraint = new RowConstraints(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            rowConstraint.setPrefHeight(45);
            grid.getRowConstraints().add(i, rowConstraint);

        }
        grid.setPrefHeight(TowerPuzzleModel.ROW_SIZE * 50);

        pieceViews = Stream.of("kkorong.png", "pkorong.png", "rud.png")
                .map(s -> "/images/" + s)
                .map(Image::new)
                .map(ImageView::new)
                .toArray(ImageView[]::new);

        populateGrid();


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
            Logger.info("invalid,can't place it here");
            selector.reset();
        } else if (selector.getPhase().equals(BoardGameMoveSelector.Phase.SELECT_TO) && model.diskProperty(row, col).get().getColors() == Colors.EMPTY) {

            selector.select(model.diskProperty(row, col).get().getPosition());
            if (selector.isInvalidSelection()) {
                selector.reset();
                Logger.info("invalid,can't place it here");
            } else {
                if (selector.isReadyToMove()) {
                    Logger.info("moved! {} => {}", selector.getFrom(), selector.getTo());
                    selector.makeMove();
                    populateGrid();
                    if (model.isSolved()) {
                        solved();
                    }
                }
            }
        } else {
            selector.reset();
        }
    }

    private void solved() {
        Logger.info("solved");
        Platform.exit();
    }


}
