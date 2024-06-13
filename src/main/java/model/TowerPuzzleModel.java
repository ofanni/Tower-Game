package model;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import lombok.Setter;
import puzzle.TwoPhaseMoveState;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the model for the Tower Puzzle game.
 * Implements the {@link TwoPhaseMoveState} interface with {@link Position}.
 * The Tower Puzzle involves moving disks of different colors and sizes
 * across three rods to achieve a specific goal state.
 */
public class TowerPuzzleModel implements TwoPhaseMoveState<Position> {

    @Setter
    private Disk[][] rods;
    public static final int COL_SIZE = 3;
    private static final int NUMBER_OF_DISKS = 4;
    public static int ROW_SIZE = NUMBER_OF_DISKS * 2;

    private ReadOnlyObjectWrapper<Disk>[][] rodsWrapper = new ReadOnlyObjectWrapper[ROW_SIZE][COL_SIZE];

    /**
     * Constructs a new {@code TowerPuzzleModel} with the specified number of disks.
     * <p>
     * Initializes the first half {@code rods} array with empty disks, then sets up the initial
     * state of the puzzle with  blue and red disks on the rods.
     * <p>
     * The disks are arranged such that the largest disk is at the bottom, and the
     * colors alternate between blue and red. The last rod is empty.
     */

    public TowerPuzzleModel() {

        this.rods = new Disk[ROW_SIZE][COL_SIZE];
        for (int i = 0; i < NUMBER_OF_DISKS; i++) {
            for (int j = 0; j < 3; j++) {
                this.rods[i][j] = new Disk(Colors.EMPTY, new Position(i, j), 0);
            }
        }
        int size = NUMBER_OF_DISKS;
        boolean isBlue = true;
        for (int i = ROW_SIZE - 1; i >= NUMBER_OF_DISKS; i--) {
            if (isBlue) {
                this.rods[i][0] = new Disk(Colors.BLUE, new Position(i, 0), size);
                this.rods[i][1] = new Disk(Colors.RED, new Position(i, 1), size);
            } else if (!isBlue) {
                this.rods[i][1] = new Disk(Colors.BLUE, new Position(i, 1), size);
                this.rods[i][0] = new Disk(Colors.RED, new Position(i, 0), size);
            }
            this.rods[i][2] = new Disk(Colors.EMPTY, new Position(i, 2), 0);

            isBlue = !isBlue;
            size -= 1;
        }
        copy();

    }


    /**
     * Copies the rods array to the rodsWrapper array.
     * This method is used to create a read-only wrapper for the rods array
     * which is necessary for JavaFX bindings.
     */
    private void copy() {
        for (int i = 0; i < this.ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                rodsWrapper[i][j] = new ReadOnlyObjectWrapper<>(rods[i][j]);
            }
        }
    }

    /**
     * Returns the read-only property of the disk at the specified position.
     *
     * @param row The row index of the position.
     * @param col The column index of the position.
     * @return The read-only property of the disk.
     */
    public ReadOnlyObjectProperty<Disk> diskProperty(int row, int col) {
        return rodsWrapper[row][col].getReadOnlyProperty();

    }

    /**
     * Checks if it is legal to move a disk from the specified position.
     *
     * @param position The position to check.
     * @return True if the disk at the specified position is not empty , otherwise false.
     */
    @Override
    public boolean isLegalToMoveFrom(Position position) {

        return getDisk(position).getColors() != Colors.EMPTY;
    }

    /**
     * Checks if the puzzle is solved.
     *
     * @return True if all red disks are placed in increasing order on the first rod,
     * and all blue disks are placed in increasing order on the second rod, otherwise false.
     */

    @Override
    public boolean isSolved() {


        boolean isRight = true;
        for (int i = NUMBER_OF_DISKS; i < rods.length; i++) {
            if (rods[i][0].getColors() != Colors.RED) {
                isRight = false;
                break;
            }
            if (rods[i][1].getColors() != Colors.BLUE) {
                isRight = false;
                break;
            }
        }
        return isRight;

    }

    /**
     * Returns the positions of the top disks on each rod.
     * If a non-empty disk is found (i.e., not at the topmost row), its position is added to the set.
     * Additionally, if the top disk position is not found for a rod, it adds a default position
     * so the top disk is basically at the bottom (empty) position.
     *
     * @return A set containing the positions of the top disks on each rod.
     */

    public Set<Position> getTopDisks() {
        Set<Position> positions = new HashSet<>();
        for (int i = 0; i < COL_SIZE; i++) {
            for (int j = 0; j < ROW_SIZE; j++) {
                if (this.rods[j][i].getColors() != Colors.EMPTY) {
                    positions.add(this.rods[j][i].getPosition());
                    break;
                }
            }
            if (positions.size() != i + 1) {
                positions.add(new Position(ROW_SIZE - 1, i));
            }
        }
        return positions;
    }

    /**
     * Returns a set of legal moves that can be made from the current state.
     * A move is considered legal if:
     * - A disk can only be placed on a rod where it is greater than or equal to the top disk, or the rod is empty.
     * - The 'from' position is not equal to the 'to' position.
     * - If the 'to' position is at row row_size-1 (so at the bottom), it must be empty.
     * - If the row is not equal to null and the 'from' position is not equal to the top position, it must follow the logic described above.
     *
     * @return A set of legal moves.
     */
    @Override
    public Set<TwoPhaseMove<Position>> getLegalMoves() {
        Set<TwoPhaseMove<Position>> moves = new HashSet<>();
        for (var fromTop : getTopDisks()) {
            if (getDisk(fromTop).getColors() != Colors.EMPTY) {
                for (var toTop : getTopDisks()) {
                    if (toTop.row() == ROW_SIZE - 1 && fromTop != toTop && getDisk(toTop).getColors() == Colors.EMPTY) {
                        var to = new Position(toTop.row(), toTop.col());
                        moves.add(new TwoPhaseMove<>(fromTop, to));

                    } else if (toTop.row() != 0 && fromTop != toTop) {
                        var to = new Position(toTop.row() - 1, toTop.col());
                        if (getDisk(fromTop).getValue() <= getDisk(toTop).getValue() && getDisk(to).getColors() == Colors.EMPTY) {
                            moves.add(new TwoPhaseMove<>(fromTop, to));
                        }

                    }
                }
            }
        }
        return moves;
    }

    /**
     * Checks if the given move is legal.
     *
     * @param positionTwoPhaseMove The move to check.
     * @return True if the move is legal according to the getLegalMoves and for the isLegalToMoveFrom method, otherwise false.
     */

    @Override
    public boolean isLegalMove(TwoPhaseMove<Position> positionTwoPhaseMove) {

        return getLegalMoves().contains(positionTwoPhaseMove) && isLegalToMoveFrom(positionTwoPhaseMove.from());

    }

    /**
     * Makes a move if it is legal.
     * This method makes the 'from' disk position empty and sets the 'to' position to the disk position,
     * updating the corresponding properties.
     *
     * @param positionTwoPhaseMove The move to make.
     */
    @Override
    public void makeMove(TwoPhaseMove<Position> positionTwoPhaseMove) {
        if (isLegalMove(positionTwoPhaseMove)) {
            var disk = getDisk(positionTwoPhaseMove.from());
            this.rods[positionTwoPhaseMove.from().row()][positionTwoPhaseMove.from().col()] = new Disk(Colors.EMPTY, positionTwoPhaseMove.from(), 0);
            this.rods[positionTwoPhaseMove.to().row()][positionTwoPhaseMove.to().col()] = new Disk(disk.getColors(), positionTwoPhaseMove.to(), disk.getValue());
            System.out.println(this);
            copy();


        }
    }


    private Disk getDisk(Position position) {
        return rods[position.row()][position.col()];
    }

    /**
     * Clones the TowerPuzzleModel.
     *
     * @return A cloned copy of the TowerPuzzleModel.
     */
    @Override
    public TwoPhaseMoveState<Position> clone() {
        TowerPuzzleModel copy;
        try {
            copy = (TowerPuzzleModel) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        Disk[][] nb = new Disk[ROW_SIZE][COL_SIZE];
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                nb[i][j] = rods[i][j];
            }
        }
        copy.rods = nb;

        return copy;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TowerPuzzleModel{");
        sb.append("\n");
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                sb.append(rods[i][j]).append("\t");
            }
            sb.append("\n");

        }
        return sb.toString();
    }

    /**
     * Checks if this TowerPuzzleModel is equal to another object.
     *
     * @param o The object to compare with.
     * @return True if the objects are equal, otherwise false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TowerPuzzleModel that = (TowerPuzzleModel) o;
        return Arrays.deepEquals(this.rods, that.rods);
    }

    /**
     * Returns the hash code value for this TowerPuzzleModel.
     *
     * @return The hash code value for this TowerPuzzleModel.
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.rods);
    }
}
