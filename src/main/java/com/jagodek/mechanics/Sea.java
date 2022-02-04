package com.jagodek.mechanics;


import com.jagodek.mechanics.helpful.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sea implements DeadObservable {
    private final int size = 10;
    private final HashMap<Position, Ship> fleet = new HashMap<>();
    private final Map<Position, StateOfSquare> squaresState = new HashMap<>();
    private final LoseObserver loseObserver;
    private String loseMessage;
    private int points = 20;
    private RefreshGridsObserver refreshGridsObserver;
    private final List<Ship> shipList = new ArrayList<>();


    public Sea(LoseObserver loseObserver) {
        this.loseObserver = loseObserver;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                squaresState.put(new Position(i, j), StateOfSquare.FREE);
            }
        }
    }

    public Sea(LoseObserver loseObserver, RefreshGridsObserver refreshGridsObserver) {
        this.refreshGridsObserver = refreshGridsObserver;
        this.loseObserver = loseObserver;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                squaresState.put(new Position(i, j), StateOfSquare.FREE);
            }
        }
    }

    public void launchShip(Ship ship) {
        ship.addDeadObserver(this);
        shipList.add(ship);
        Position position = ship.getPosition();
        int i = position.getX();
        int j = position.getY();
        int length = ship.getLength();
        Orientation orientation = ship.getOrientation();
        if (orientation == Orientation.HORIZONTAL) {
            for (int a = i - 1; a < 2 + i; a++) {
                for (int b = j - 1; b < j + 1 + length; b++) {
                    if (squaresState.containsKey(new Position(a, b))) {
                        squaresState.replace(new Position(a, b), StateOfSquare.TOOCLOSE);
                    }
                }
            }
            for (int k = j; k < length + j; k++) {
                squaresState.replace(new Position(i, k), StateOfSquare.OCCUPIED);
                fleet.put(new Position(i, k), ship);
            }
        } else {
            for (int a = i - 1; a < i + 1 + length; a++) {
                for (int b = j - 1; b < j + 2; b++) {
                    if (squaresState.containsKey(new Position(a, b))) {
                        squaresState.replace(new Position(a, b), StateOfSquare.TOOCLOSE);
                    }
                }
            }
            for (int k = i; k < i + length; k++) {
                squaresState.replace(new Position(k, j), StateOfSquare.OCCUPIED);
                fleet.put(new Position(k, j), ship);
            }
        }
    }

    public boolean canBePlaced(Position position, Orientation orientation, int length) {
        int i = position.getX();
        int j = position.getY();
        if (orientation == Orientation.HORIZONTAL) {
            if (length + j > 10) {
                return false;
            }
        } else {
            if (length + i > 10) {
                return false;
            }
        }

        for (int k = 0; k < length; k++) {
            if (squaresState.get(position) != StateOfSquare.FREE) {
                return false;
            }
            position = position.incrementByOrientation(orientation);
        }
        return true;
    }

    public StateOfSquare stateOfSquare(Position p) {
        return squaresState.get(p);
    }

    public void hit(Position position) {
        if (stateOfSquare(position) == StateOfSquare.OCCUPIED) {
            squaresState.replace(position, StateOfSquare.HIT);
            points -= 1;
            fleet.get(position).lowerHealth();
            if (points == 0) {
                loseObserver.loss(loseMessage);
            }
        } else if (stateOfSquare(position) == StateOfSquare.TOOCLOSE || stateOfSquare(position) == StateOfSquare.FREE) {
            squaresState.replace(position, StateOfSquare.MISSED);
        }
    }


    public void setLoseMessage(String loseMessage) {
        this.loseMessage = loseMessage;
    }

    public void dead(Ship ship) {
        Position position = ship.getPosition();
        int length = ship.getLength();
        int i = position.getX();
        int j = position.getY();
        Orientation orientation = ship.getOrientation();
        if (orientation == Orientation.HORIZONTAL) {
            for (int a = i - 1; a < 2 + i; a++) {
                for (int b = j - 1; b < j + 1 + length; b++) {
                    if (squaresState.containsKey(new Position(a, b))) {
                        squaresState.replace(new Position(a, b), StateOfSquare.MISSED);
                    }
                }
            }
            for (int k = j; k < length + j; k++) {
                squaresState.replace(new Position(i, k), StateOfSquare.SUNK);
            }
        } else {
            for (int a = i - 1; a < i + 1 + length; a++) {
                for (int b = j - 1; b < j + 2; b++) {
                    if (squaresState.containsKey(new Position(a, b))) {
                        squaresState.replace(new Position(a, b), StateOfSquare.MISSED);
                    }
                }
            }
            for (int k = i; k < i + length; k++) {
                squaresState.replace(new Position(k, j), StateOfSquare.SUNK);
            }
        }
        if (refreshGridsObserver != null)
            refreshGridsObserver.refreshGrids();
    }

    public boolean containsShip(Ship ship) {
        for (Map.Entry<Position, Ship> entry : fleet.entrySet()) {
            if (ship.equals(entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    public List<Ship> getShipList() {
        return shipList;
    }
}