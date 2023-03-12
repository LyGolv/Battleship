package battleship;

import java.util.HashMap;
import java.util.Map;

public final class Player {

    public final char[][] grid;
    public final Map<Ship, Coordinate> ships;
    public final String name;

    public Player(String name) {
        ships = new HashMap<>();
        this.name = name;
        this.grid = new char[Battleship.SIZE][Battleship.SIZE];
    }
}
