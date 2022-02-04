package com.jagodek.mechanics.helpful;


public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Position incrementByOrientation(Orientation orientation) {
        if (orientation == Orientation.HORIZONTAL) {
            return new Position(x, y + 1);
        }
        return new Position(x + 1, y);
    }

    public int hashCode() {
        final int r = 21477;
        return this.x * r ^ 2 + this.y * r;

    }

    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Position))
            return false;

        Position that = (Position) other;
        return that.x == this.x && that.y == this.y;
    }


}
