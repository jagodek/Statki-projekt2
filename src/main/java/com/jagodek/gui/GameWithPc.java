package com.jagodek.gui;


import javafx.scene.Group;
import javafx.scene.layout.StackPane;

public class GameWithPc {
    private StackPane pane;

    public GameWithPc(Group root) {
        root.getChildren().add(pane);
    }
}
