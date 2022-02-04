package com.jagodek.mechanics.helpful;

public enum StateOfSquare {
    FREE,
    OCCUPIED,
    HIT,
    SUNK,
    TOOCLOSE,
    MISSED;

    public String color() {
        if (this == OCCUPIED) {
            return "-fx-background-color:#1d3861;";
        }
        if (this == HIT) {
            return "-fx-background-color:#cc6600;";
        }
        if (this == SUNK) {
            return "-fx-background-color:#cc0000;";
        }
        if (this == MISSED) {
            return "-fx-background-color:#999999;";
        }
        return "-fx-background-color:transparent;";
    }
}