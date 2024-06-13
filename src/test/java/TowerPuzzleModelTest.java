

import javafx.beans.property.ReadOnlyObjectProperty;
import model.Colors;
import model.Disk;
import model.Position;
import model.TowerPuzzleModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import puzzle.TwoPhaseMoveState;

import java.util.Set;

import static model.TowerPuzzleModel.ROW_SIZE;
import static org.junit.jupiter.api.Assertions.*;

public class TowerPuzzleModelTest {


    private TowerPuzzleModel model;

    @BeforeEach
    void setUp() {
        model = new TowerPuzzleModel();
    }


    @Test
    public void testInitialSetup() {
        for (int i = 0; i < ROW_SIZE; i++) {
            assertEquals(Colors.EMPTY, model.diskProperty(i, 2).get().getColors());
        }
        assertEquals(Colors.BLUE, model.diskProperty(7, 0).get().getColors());
        assertEquals(Colors.RED, model.diskProperty(7, 1).get().getColors());


    }

    @Test
    void testDiskProperty() {
        ReadOnlyObjectProperty<Disk> diskProperty = model.diskProperty(4, 0);
        assertEquals(Colors.RED, diskProperty.get().getColors());
        assertEquals(1, diskProperty.get().getValue());
    }

    @Test
    public void testGetTopDisks() {
        Set<Position> topDisks = model.getTopDisks();
        assertEquals(3, topDisks.size());

        assertTrue(topDisks.contains(new Position(4, 0)));
        assertTrue(topDisks.contains(new Position(4, 1)));
        assertTrue(topDisks.contains(new Position(ROW_SIZE - 1, 2)));
    }

    @Test
    public void testIsLegalToMoveFrom() {
        assertTrue(model.isLegalToMoveFrom(new Position(4, 0)));
        assertTrue(model.isLegalToMoveFrom(new Position(4, 1)));

        assertFalse(model.isLegalToMoveFrom(new Position(0, 0)));
        assertFalse(model.isLegalToMoveFrom(new Position(1, 1)));
        assertFalse(model.isLegalToMoveFrom(new Position(0, 2)));
    }

    @Test
    public void testIsLegalMove() {

        TwoPhaseMoveState.TwoPhaseMove<Position> legalMove = new TwoPhaseMoveState.TwoPhaseMove<>(new Position(4, 0), new Position(ROW_SIZE - 1, 2));
        assertTrue(model.isLegalMove(legalMove));

        TwoPhaseMoveState.TwoPhaseMove<Position> illegalMove = new TwoPhaseMoveState.TwoPhaseMove<>(new Position(4, 0), new Position(4, 1));
        assertFalse(model.isLegalMove(illegalMove));
    }


    @Test
    public void testGetLegalMoves() {
        Set<TwoPhaseMoveState.TwoPhaseMove<Position>> legalMoves = model.getLegalMoves();
        assertFalse(legalMoves.isEmpty());
    }

    @Test
    public void testIsSolved() {
        assertFalse(model.isSolved());
        int size = 1;
        Disk[][] rods = new Disk[8][3];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                rods[i][j] = new Disk(Colors.EMPTY, new Position(i, j), 0);
            }
        }
        for (int i = 4; i <= 7; i++) {
            rods[i][0] = new Disk(Colors.RED, new Position(i, 0), size);
            rods[i][1] = new Disk(Colors.BLUE, new Position(i, 1), size);

            size += 1;
        }
        model.setRods(rods);


        assertTrue(model.isSolved());
    }

    @Test
    public void testMakeMove() {
        TwoPhaseMoveState.TwoPhaseMove<Position> move = new TwoPhaseMoveState.TwoPhaseMove<>(new Position(4, 0), new Position(ROW_SIZE - 1, 2));
        model.makeMove(move);

        assertEquals(Colors.EMPTY, model.diskProperty(4, 0).get().getColors());
        assertEquals(Colors.RED, model.diskProperty(ROW_SIZE - 1, 2).get().getColors());
    }


    @Test
    public void testClone() {
        TowerPuzzleModel clonedModel = (TowerPuzzleModel) model.clone();
        assertEquals(model, clonedModel);

        TwoPhaseMoveState.TwoPhaseMove<Position> move = new TwoPhaseMoveState.TwoPhaseMove<>(new Position(4, 0), new Position(ROW_SIZE - 1, 2));
        clonedModel.makeMove(move);
        assertNotEquals(model, clonedModel);
    }

    @Test
    public void testEqualsAndHashCode() {
        TowerPuzzleModel anotherModel = new TowerPuzzleModel();
        assertEquals(model, anotherModel);
        assertEquals(model.hashCode(), anotherModel.hashCode());
    }
}
