package com.jagodek.mechanics;

import com.jagodek.mechanics.helpful.*;
import javafx.geometry.Pos;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EasyPC implements Enemy, DeadObservable {
    private final Sea mySea;
    private final Sea enemySea;
    private final Random random = new Random();
    private final int[][] fleet = {{4, 1}, {3, 2}, {2, 3}, {1, 4}};
    private final Comparator<Position> positionComparator = new PositionComparator();
    private final Set<Position> toBeHit = new TreeSet<>(positionComparator);


    public EasyPC(Sea mySea, Sea enemySea) {
        this.enemySea = enemySea;
        this.mySea = mySea;
        for (int i = 0; i < 10; i++) {
            for (int k = 0; k < 10; k++) {
                toBeHit.add(new Position(i, k));
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < fleet[i][1]; j++) {
                while (true) {
                    int length = fleet[i][0];
                    int orientationInt = random.nextInt(2);
                    Orientation orientation = Orientation.VERTICAL;
                    if (orientationInt == 1) {
                        orientation = Orientation.HORIZONTAL;
                    }
                    Position position = new Position(random.nextInt(10), random.nextInt(10));
                    if (enemySea.canBePlaced(position, orientation, length)) {
                        Ship ship = new Ship(length, position, orientation);
                        ship.addDeadObserver(this);
                        enemySea.launchShip(ship);
                        break;
                    }

                }

            }
        }
    }

    @Override
    public List<Position> makeHit() {
        Position toHit = randomFromSet();
        toBeHit.remove(toHit);
        mySea.hit(toHit);
        if (mySea.stateOfSquare(toHit) == StateOfSquare.HIT) { //jeśli strzał był celny przeciwnik ma jeszcze jeden ruch
            List<Position> arrTemp = makeHit();
            List<Position> temp = new ArrayList<>();
            temp.add(toHit);
            List<Position> resultList = Stream.concat(arrTemp.stream(), temp.stream()).collect(Collectors.toList());
            return resultList;
        }

        List<Position> temp = new ArrayList<>();
        temp.add(toHit);
        return temp;
    }

    @Override
    public void giveShipList(List<Ship> ships) {
        for (Ship ship : ships) {
            ship.addDeadObserver(this);
        }
    }

    private Position randomFromSet() {
        int rand = random.nextInt(toBeHit.size());
        int i = 0;
        for (Position position : toBeHit) {
            if (i == rand) {
                return position;
            }
            i++;
        }
        return null;
    }

    @Override
    public void dead(Ship ship) {

        if (mySea.containsShip(ship)) {
            Orientation orientation = ship.getOrientation();
            int i = ship.getPosition().getX();
            int j = ship.getPosition().getY();
            int length = ship.getLength();
            if (orientation == Orientation.HORIZONTAL) {
                for (int a = i - 1; a < 2 + i; a++) {
                    for (int b = j - 1; b < j + 1 + length; b++) {
                        if (toBeHit.contains(new Position(a, b))) {
                            toBeHit.remove(new Position(a, b));
                            System.out.println(new Position(a, b));
                        }
                    }
                }
            } else {
                for (int a = i - 1; a < i + 1 + length; a++) {
                    for (int b = j - 1; b < j + 2; b++) {
                        toBeHit.remove(new Position(a, b));
                    }
                }
            }
        }
    }
}
