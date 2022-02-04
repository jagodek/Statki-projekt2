package com.jagodek.gui;

import com.jagodek.mechanics.*;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BattleShipApplication extends Application {
    private final Group root = new Group();
    private Button pcButton, otherPlayerButton;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setAlwaysOnTop(true);
        scene = new Scene(root, 700, 800);
        primaryStage.setTitle("Battleships");
        primaryStage.setScene(scene);
        setMenu1();
        primaryStage.show();
    }

    private void setMenu1() {
        StackPane menu1 = new StackPane();
        menu1.setPrefSize(700, 800);
        VBox enemyChoice = new VBox();
        enemyChoice.setSpacing(15);
        enemyChoice.setAlignment(Pos.BASELINE_CENTER);
        Label enemyChoiceLabel = new Label("Wybierz przeciwnika");
        enemyChoiceLabel.setFont(Font.font(20));
        pcButton = new Button("Komputer");
        pcButton.setFont(Font.font(20));
        otherPlayerButton = new Button("Inny gracz");
        otherPlayerButton.setFont(Font.font(20));
        enemyChoice.getChildren().addAll(enemyChoiceLabel, pcButton, otherPlayerButton);
        StackPane.setAlignment(enemyChoice, Pos.BASELINE_CENTER);
        menu1.setLayoutY(200);
        menu1.getChildren().add(enemyChoice);
        root.getChildren().add(menu1);
        pcButton.setOnAction(f -> {
            root.getChildren().clear();
            setMenuPc();
        });
        otherPlayerButton.setOnAction(f -> {
            root.getChildren().clear();
            new PlayersBoards(root);
        });
    }

    private void setMenuPc() {
        StackPane menuPc = new StackPane();
        menuPc.setPrefSize(700, 800);
        VBox levelChoice = new VBox();
        levelChoice.setSpacing(15);
        levelChoice.setAlignment(Pos.BASELINE_CENTER);
        Label enemyChoiceLabel = new Label("Wybierz poziom trudnosci");
        enemyChoiceLabel.setFont(Font.font(20));
        Button easierButton = new Button("Latwiejszy");
        easierButton.setFont(Font.font(20));
        Button harderButton = new Button("Trudniejszy");
        harderButton.setFont(Font.font(20));
        levelChoice.getChildren().addAll(enemyChoiceLabel, easierButton, harderButton);
        StackPane.setAlignment(levelChoice, Pos.BASELINE_CENTER);
        menuPc.setLayoutY(200);
        menuPc.getChildren().add(levelChoice);
        root.getChildren().add(menuPc);
        easierButton.setOnAction(f -> {
            root.getChildren().clear();
            new Boards(root, 0);
        });
        harderButton.setOnAction(f -> {
            root.getChildren().clear();
            new Boards(root, 1);
        });

    }

    private void setMenuOtherPlayer() {
        StackPane menuOtherPlayer = new StackPane();
        menuOtherPlayer.setPrefSize(700, 800);
        VBox serverParameters = new VBox();
    }


}
