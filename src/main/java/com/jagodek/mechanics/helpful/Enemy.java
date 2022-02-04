package com.jagodek.mechanics.helpful;


import com.jagodek.mechanics.Ship;

import java.util.ArrayList;
import java.util.List;

public interface Enemy {
    List<Position> makeHit();

    void giveShipList(List<Ship> ships);
}
