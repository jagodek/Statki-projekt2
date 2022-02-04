package com.jagodek.mechanics.helpful;

import javafx.util.Pair;

import java.util.Comparator;

public class PositionComparator implements Comparator<Position> {
    public int compare(Position p1, Position p2) {
        if (p1.getX() != p2.getX()) {
            return Integer.compare(p1.getX(), p2.getX());
        }
        return Integer.compare(p1.getY(), p2.getY());
    }
}
