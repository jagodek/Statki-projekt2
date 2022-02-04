package com.jagodek.gui;

import com.jagodek.mechanics.EasyPC;
import com.jagodek.mechanics.HardPC;
import com.jagodek.mechanics.helpful.*;
import com.jagodek.mechanics.Sea;
import com.jagodek.mechanics.Ship;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.sql.Ref;
import java.util.*;

public class Boards implements LoseObserver, RefreshGridsObserver {
    private final StackPane pane = new StackPane();
    private final int boxSize = 30;
    private final int[][] fleet = {{4, 1}, {3, 2}, {2, 3}, {1, 4}};
    private final Queue<Integer> toBePlaced = new LinkedList<>();
    private Orientation defaultOrientation = Orientation.VERTICAL;
    private final Map<Position, VBox> squares = new HashMap<>();
    private final Map<Position, VBox> enemSsquares = new HashMap<>();
    private int currentLength;
    private boolean ready = false;
    private boolean loss = false;
    private Enemy enemy;
    private boolean yourTurn = true;
    private final Set<Position> toBeHit = new TreeSet<>(new PositionComparator());
    private Label startLabel;
    private final Sea mySea;
    private final Sea enemySea;


    public Boards(Group root, int enemyIndicator) {
        root.getChildren().add(pane);
        VBox boards = new VBox();
        pane.getChildren().add(boards);
        VBox myBox = new VBox();
        Label myBoardLabel = new Label("Moja plansza");
        HBox boardAndShips = new HBox();
        myBox.getChildren().addAll(myBoardLabel, boardAndShips);
        GridPane myGrid = new GridPane();
        Button changeOrientation = new Button("POZIOM/PION");
        changeOrientation.setTranslateX(10);
        changeOrientation.setOnAction(f -> {
            defaultOrientation = defaultOrientation.switchOrientation();
        });
        boardAndShips.getChildren().addAll(myGrid, changeOrientation);
        myGrid.setGridLinesVisible(true);

        mySea = new Sea(this, this);
        enemySea = new Sea(this, this);
        mySea.setLoseMessage("Przegrales");
        enemySea.setLoseMessage("Wygrales");


        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < fleet[i][1]; j++) {
                toBePlaced.add(fleet[i][0]);
            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                toBeHit.add(new Position(i, j));
            }
        }


        for (int i = 0; i < 11; i++) {   // ustawienie planszy gracza
            myGrid.getRowConstraints().add(new RowConstraints(boxSize));
            myGrid.getColumnConstraints().add(new ColumnConstraints(boxSize));
            for (int j = 0; j < 11; j++) {
                if ((i == 0) && (j > 0)) {
                    String cordValue = String.valueOf(j);
                    Label cord = new Label(cordValue);
                    cord.setAlignment(Pos.CENTER);
                    cord.setFont(Font.font(boxSize * 0.6));
                    cord.setTranslateX(boxSize * 0.3);
                    myGrid.add(cord, j, i);
                } else if ((j == 0) && (i > 0)) {
                    String cordValue = Character.toString((char) (i + 64));
                    Label cord = new Label(cordValue);
                    cord.setAlignment(Pos.CENTER);
                    cord.setFont(Font.font(boxSize * 0.6));
                    cord.setTranslateX(boxSize * 0.3);
                    myGrid.add(cord, j, i);
                } else {
                    VBox space = new VBox();
                    if ((i > 0) && (j > 0)) {
                        int finalI = i - 1;
                        int finalJ = j - 1;
                        Position positionPointed = new Position(finalI, finalJ);
                        squares.put(positionPointed, space);
                        space.setStyle("-fx-background-color:transparent;");


                        space.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                if (!toBePlaced.isEmpty()) {

                                    if (mySea.canBePlaced(positionPointed, defaultOrientation, currentLength)) {
                                        mySea.launchShip(new Ship(currentLength, positionPointed, defaultOrientation));
                                        toBePlaced.remove();
                                        if (toBePlaced.isEmpty()) {
                                            ready = true;
                                            boardAndShips.getChildren().remove(changeOrientation);
                                            startLabel = new Label("Wykonaj pierwszy ruch");
                                            boardAndShips.getChildren().add(startLabel);
                                            startLabel.setTranslateX(30);
                                            startLabel.setFont(Font.font(20));
                                            yourTurn = true;
                                            enemy.giveShipList(mySea.getShipList());
                                        }
                                    }
                                }

                            }
                        });

                        space.setOnMouseEntered(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                if (!toBePlaced.isEmpty()) {
                                    currentLength = toBePlaced.peek();
                                    Position nextPosition = positionPointed;

                                    if (mySea.canBePlaced(new Position(finalI, finalJ), defaultOrientation, currentLength)) {
                                        VBox otherSpace = squares.get(positionPointed);
                                        for (int k = 0; k < currentLength; k++) {
                                            otherSpace.setStyle("-fx-background-color:#00ff00;");
                                            nextPosition = nextPosition.incrementByOrientation(defaultOrientation);
                                            otherSpace = squares.get(nextPosition);
                                        }
                                    }
                                }
                            }
                        });
                        space.setOnMouseExited(new EventHandler<MouseEvent>() {

                            @Override
                            public void handle(MouseEvent t) {
                                VBox otherSpace = squares.get(positionPointed);
                                Position nextPosition = positionPointed;
                                for (int a = 0; a < 4; a++) {

                                    otherSpace.setStyle(mySea.stateOfSquare(nextPosition).color());
                                    if (squares.containsKey(nextPosition.incrementByOrientation(defaultOrientation))) {
                                        nextPosition = nextPosition.incrementByOrientation(defaultOrientation);
                                        otherSpace = squares.get(nextPosition);
                                    } else {
                                        break;
                                    }
                                }
                            }
                        });
                    }

                    myGrid.add(space, j, i);
                }
            }
        }
        VBox enemyBox = new VBox();
        if (enemyIndicator == 0) {
            enemy = new EasyPC(mySea, enemySea);
        } else if (enemyIndicator == 1) {
            enemy = new HardPC(mySea, enemySea);
        }


        Label enemyBoardLabel = new Label("Plansza przeciwnika");
        GridPane enemyGrid = new GridPane();
        enemyBox.getChildren().addAll(enemyBoardLabel, enemyGrid);
        enemyGrid.setGridLinesVisible(true);

        for (int i = 0; i < 11; i++) {  //narysowanie planszy przeciwnika
            enemyGrid.getRowConstraints().add(new RowConstraints(boxSize));
            enemyGrid.getColumnConstraints().add(new ColumnConstraints(boxSize));
            for (int j = 0; j < 11; j++) {
                if ((i == 0) && (j > 0)) {
                    String cordValue = String.valueOf(j);
                    Label cord = new Label(cordValue);
                    cord.setAlignment(Pos.CENTER);
                    cord.setFont(Font.font(boxSize * 0.6));
                    cord.setTranslateX(boxSize * 0.3);
                    enemyGrid.add(cord, j, i);
                } else if ((j == 0) && (i > 0)) {
                    String cordValue = Character.toString((char) (i + 64));
                    Label cord = new Label(cordValue);
                    cord.setAlignment(Pos.CENTER);
                    cord.setFont(Font.font(boxSize * 0.6));
                    cord.setTranslateX(boxSize * 0.3);
                    enemyGrid.add(cord, j, i);
                } else {
                    VBox space = new VBox();
                    if (i > 0 && j > 0) {
                        int finalI = i - 1;
                        int finalJ = j - 1;
                        Position positionPointed = new Position(finalI, finalJ);
                        enemSsquares.put(positionPointed, space);
                        space.setOnMouseClicked(f -> {
                            if (ready && yourTurn && toBeHit.contains(positionPointed)) {
                                enemySea.hit(positionPointed);
                                space.setStyle(enemySea.stateOfSquare(positionPointed).color());
                                toBeHit.remove(positionPointed);
                                if (!loss && enemySea.stateOfSquare(positionPointed) != StateOfSquare.HIT && enemySea.stateOfSquare(positionPointed) != StateOfSquare.SUNK) {
                                    yourTurn = false;
                                    startLabel.setText("Ruch przeciwnika");
                                    List<Position> hit = enemy.makeHit();
                                    for (Position positionHit : hit) {
                                        squares.get(positionHit).setStyle(mySea.stateOfSquare(positionHit).color());
                                    }
                                    yourTurn = true;
                                    startLabel.setText("Twoj ruch");
                                }

                            }
                        });
                    }
                    enemyGrid.add(space, j, i);
                }
            }
        }

        boards.getChildren().addAll(myBox, enemyBox);


        boards.setSpacing(10);
        boards.setTranslateX(30);


    }


    @Override
    public void loss(String lossMessage) {
        yourTurn = false;
        startLabel.setText(lossMessage);
        loss = true;
    }

    @Override
    public void refreshGrids() {
        for (Map.Entry<Position, VBox> entry : squares.entrySet()) {
            Position position = entry.getKey();
            VBox box = entry.getValue();
            box.setStyle(mySea.stateOfSquare(position).color());

        }
        for (Map.Entry<Position, VBox> entry : enemSsquares.entrySet()) {
            Position position = entry.getKey();
            VBox box = entry.getValue();
            if (enemySea.stateOfSquare(position) != StateOfSquare.OCCUPIED)
                box.setStyle(enemySea.stateOfSquare(position).color());
            if (toBeHit.contains(position) && enemySea.stateOfSquare(position) == StateOfSquare.MISSED)
                toBeHit.remove(position);
        }
    }
}
