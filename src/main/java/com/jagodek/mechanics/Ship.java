package com.jagodek.mechanics;


import com.jagodek.mechanics.helpful.DeadObservable;
import com.jagodek.mechanics.helpful.Orientation;
import com.jagodek.mechanics.helpful.Position;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    private final int length;
    private final Position position;
    private final Orientation orientation;
    private int health;
    private final List<DeadObservable> deadObservables = new ArrayList<>();

    public Ship(int length, Position position, Orientation orientation) {
        this.length = length;
        this.health = length;
        this.position = position;
        this.orientation = orientation;
    }


    public void addDeadObserver(DeadObservable deadObservable) {
        this.deadObservables.add(deadObservable);
    }

    public void updateDeadObs() {
        deadObservables.forEach(f -> f.dead(this));
    }

    public Position getPosition() {
        return position;
    }

    public int getLength() {
        return length;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void lowerHealth() {
        health -= 1;
        if (health == 0) {
            updateDeadObs();
        }
    }
}
