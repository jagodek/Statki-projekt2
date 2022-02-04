package com.jagodek.mechanics.helpful;

public enum Orientation {
    VERTICAL,
    HORIZONTAL;

    public Orientation switchOrientation() {
        if (this == VERTICAL)
            return HORIZONTAL;
        return VERTICAL;
    }
}
