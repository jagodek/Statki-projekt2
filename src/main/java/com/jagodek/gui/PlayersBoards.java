package com.jagodek.gui;

import com.jagodek.mechanics.EasyPC;
import com.jagodek.mechanics.HardPC;
import com.jagodek.mechanics.Sea;
import com.jagodek.mechanics.Ship;
import com.jagodek.mechanics.helpful.*;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.util.*;

public class PlayersBoards implements LoseObserver {
    private final StackPane pane = new StackPane();
    private final int boxSize = 30;
    private final int[][] fleet = {{4, 1}, {3, 2}, {2, 3}, {1, 4}};
    private final Queue<Integer> toBePlaced = new LinkedList<>();
    private final Queue<Integer> enemyToBePlaced = new LinkedList<>();
    private Orientation defaultOrientation = Orientation.VERTICAL;
    private final Map<Position, VBox> squares = new HashMap<>();
    private final Map<Position, VBox> enemSsquares = new HashMap<>();
    private int currentLength;
    private final boolean ready = false;
    private boolean loss = false;
    private boolean p1Turn = true;
    private final Set<Position> toBeHit = new TreeSet<>(new PositionComparator());
    private final Set<Position> enemyToBeHit = new TreeSet<>(new PositionComparator());
    private Label startLabel;
    private final Sea mySea;
    private final Sea enemySea;
    private final Label Label1 = new Label("Teraz statki ustawia gracz 2");
    private final Button gotowe1 = new Button("Gotowy");
    private final Button gotowe2 = new Button("Gotowy");
    private boolean bothReady = false;
    private final VBox boards;
    private final VBox enemyBox;
    private final VBox myBox;

    public PlayersBoards(Group root) {
        root.getChildren().add(pane);
        boards = new VBox();
        pane.getChildren().add(boards);
        myBox = new VBox();
        Label myBoardLabel = new Label("Plansza gracza 1");
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

        mySea = new Sea(this);
        enemySea = new Sea(this);
        mySea.setLoseMessage("Wygral gracz nr2");
        enemySea.setLoseMessage("Wygral gracz nr1");


        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < fleet[i][1]; j++) {
                toBePlaced.add(fleet[i][0]);
                enemyToBePlaced.add(fleet[i][0]);
            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                toBeHit.add(new Position(i, j));
                enemyToBeHit.add(new Position(i, j));
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
                                if (!toBePlaced.isEmpty() && p1Turn) {

                                    if (mySea.canBePlaced(positionPointed, defaultOrientation, currentLength)) {
                                        mySea.launchShip(new Ship(currentLength, positionPointed, defaultOrientation));
                                        toBePlaced.remove();
                                        if (toBePlaced.isEmpty()) {
                                            space.setStyle(mySea.stateOfSquare(positionPointed).color());
                                            boardAndShips.getChildren().add(gotowe1);
                                            gotowe1.setTranslateX(10);
                                            gotowe1.setOnAction(f -> {
                                                boardAndShips.getChildren().add(Label1);
                                                Label1.setTranslateX(-50);
                                                Label1.setTranslateY(30);
                                                Label1.setFont(Font.font(20));
                                                player2view();
                                                p1Turn = false;
                                                boardAndShips.getChildren().remove(gotowe1);
                                            });
                                        }
                                    }
                                } else if (!p1Turn && enemyToBePlaced.isEmpty() && enemyToBeHit.contains(positionPointed) && bothReady) {
                                    mySea.hit(positionPointed);
                                    enemyToBeHit.remove(positionPointed);
                                    squares.get(positionPointed).setStyle(mySea.stateOfSquare(positionPointed).color());

                                    p1Turn = true;
                                    transit("Kolej gracza 1");
                                    player1view();
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
                                if (!toBePlaced.isEmpty()) {
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
                            }
                        });
                    }

                    myGrid.add(space, j, i);
                }
            }
        }
        enemyBox = new VBox();
        Label enemyBoardLabel = new Label("Plansza gracza 2");
        GridPane enemyGrid = new GridPane();
        enemyBox.getChildren().addAll(enemyBoardLabel, enemyGrid);
        enemyGrid.setGridLinesVisible(true);

        for (int i = 0; i < 11; i++) {
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
                        space.setOnMouseEntered(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                if (!enemyToBePlaced.isEmpty() && !p1Turn) {
                                    currentLength = enemyToBePlaced.peek();
                                    Position nextPosition = positionPointed;

                                    if (enemySea.canBePlaced(new Position(finalI, finalJ), defaultOrientation, currentLength)) {
                                        VBox otherSpace = enemSsquares.get(positionPointed);
                                        for (int k = 0; k < currentLength; k++) {
                                            otherSpace.setStyle("-fx-background-color:#00ff00;");
                                            nextPosition = nextPosition.incrementByOrientation(defaultOrientation);
                                            otherSpace = enemSsquares.get(nextPosition);
                                        }
                                    }
                                }
                            }
                        });
                        space.setOnMouseExited(new EventHandler<MouseEvent>() {

                            @Override
                            public void handle(MouseEvent t) {
                                if (!enemyToBePlaced.isEmpty()) {
                                    VBox otherSpace = enemSsquares.get(positionPointed);
                                    Position nextPosition = positionPointed;
                                    for (int a = 0; a < 4; a++) {

                                        otherSpace.setStyle(enemySea.stateOfSquare(nextPosition).color());
                                        if (enemSsquares.containsKey(nextPosition.incrementByOrientation(defaultOrientation))) {
                                            nextPosition = nextPosition.incrementByOrientation(defaultOrientation);
                                            otherSpace = enemSsquares.get(nextPosition);
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }
                        });

                        space.setOnMouseClicked(f -> {
                            if (!enemyToBePlaced.isEmpty() && !p1Turn) {
                                if (enemySea.canBePlaced(positionPointed, defaultOrientation, currentLength)) {
                                    enemySea.launchShip(new Ship(currentLength, positionPointed, defaultOrientation));
                                    enemyToBePlaced.remove();
                                    if (enemyToBePlaced.isEmpty()) {
                                        boardAndShips.getChildren().remove(Label1);
                                        space.setStyle(enemySea.stateOfSquare(positionPointed).color());
                                        boardAndShips.getChildren().remove(changeOrientation);
                                        boardAndShips.getChildren().add(gotowe2);
                                        gotowe2.setOnAction(e -> {
                                            boardAndShips.getChildren().remove(gotowe2);
                                            boardAndShips.getChildren().remove(Label1);
                                            p1Turn = true;
                                            bothReady = true;
                                            transit("Kolej gracza 1");
                                            player1view();
                                        });

                                    }
                                }
                            } else if (p1Turn && toBeHit.contains(positionPointed) && enemyToBePlaced.isEmpty() && bothReady) {
                                enemySea.hit(positionPointed);
                                toBeHit.remove(positionPointed);
                                enemSsquares.get(positionPointed).setStyle(enemySea.stateOfSquare(positionPointed).color());
                                p1Turn = false;
                                transit("Kolej gracza 2");
                                player2view();
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
        p1Turn = false;
        startLabel.setText(lossMessage);
        loss = true;
    }


    private void player2view() {
        for (Map.Entry<Position, VBox> entry : squares.entrySet()) {
            Position position = entry.getKey();
            VBox box = entry.getValue();
            if (mySea.stateOfSquare(position) != StateOfSquare.OCCUPIED)
                box.setStyle(mySea.stateOfSquare(position).color());
            else if (mySea.stateOfSquare(position) == StateOfSquare.OCCUPIED)
                box.setStyle("-fx-background-color: transparent");
            if (enemyToBeHit.contains(position) && mySea.stateOfSquare(position) == StateOfSquare.MISSED)
                enemyToBeHit.remove(position);

        }
        for (Map.Entry<Position, VBox> entry : enemSsquares.entrySet()) {
            Position position = entry.getKey();
            VBox box = entry.getValue();
            box.setStyle(enemySea.stateOfSquare(position).color());
            if (toBeHit.contains(position) && enemySea.stateOfSquare(position) == StateOfSquare.MISSED)
                toBeHit.remove(position);
        }
    }

    private void player1view() {
        for (Map.Entry<Position, VBox> entry : squares.entrySet()) {
            Position position = entry.getKey();
            VBox box = entry.getValue();
            box.setStyle(mySea.stateOfSquare(position).color());
            if (enemyToBeHit.contains(position) && mySea.stateOfSquare(position) == StateOfSquare.MISSED)
                enemyToBeHit.remove(position);

        }
        for (Map.Entry<Position, VBox> entry : enemSsquares.entrySet()) {
            Position position = entry.getKey();
            VBox box = entry.getValue();
            if (enemySea.stateOfSquare(position) != StateOfSquare.OCCUPIED)
                box.setStyle(enemySea.stateOfSquare(position).color());
            else if (enemySea.stateOfSquare(position) == StateOfSquare.OCCUPIED)
                box.setStyle("-fx-background-color: transparent");
            if (toBeHit.contains(position) && enemySea.stateOfSquare(position) == StateOfSquare.MISSED)
                toBeHit.remove(position);
        }
    }

    private final Button changePlayer = new Button();

    private void transit(String message) {
        boards.getChildren().clear();
        boards.getChildren().add(changePlayer);
        changePlayer.setTranslateX(300);
        changePlayer.setTranslateX(300);
        changePlayer.setFont(Font.font(25));
        changePlayer.setText(message);
        changePlayer.setOnAction(f -> {
            boards.getChildren().clear();
            boards.getChildren().addAll(myBox, enemyBox);
        });

    }
}

