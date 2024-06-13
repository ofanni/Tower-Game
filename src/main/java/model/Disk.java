package model;

import java.util.Objects;

/**
 * Represents a disk in the Tower Puzzle game with a specific color, position, and value.
 */
public class Disk {
    private Colors colors;
    private Position position;
    private int value;

    /**
     * Constructs a new {@code Disk} with the specified color, position, and value.
     *
     * @param colors   The color of the disk which can be blue,red or empty.
     * @param position The position of the disk.
     * @param value    The value of the disk.
     */

    public Disk(Colors colors, Position position, int value) {
        this.colors = colors;
        this.position = position;
        this.value = value;
    }

    public Colors getColors() {
        return colors;
    }

    public Position getPosition() {
        return position;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Disk disk = (Disk) o;
        return value == disk.value && colors == disk.colors && position.equals(disk.position);
    }


    @Override
    public int hashCode() {
        return Objects.hash(colors, position, value);
    }

    /**
     * Returns a string representation of the disk. The string representation consists of the
     * disk's color and value.
     *
     * @return A string representation of the disk.
     */
    @Override
    public String toString() {

        return switch (colors) {
            case RED -> "R" + value;
            case BLUE -> "B" + value;
            case EMPTY -> "E" + value;
        };
    }
}
