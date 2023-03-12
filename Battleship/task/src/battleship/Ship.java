package battleship;

public enum Ship {
    AIRCRAFT_CARRIER(5, "Aircraft carrier"),
    BATTLESHIP(4, "Battleship"),
    SUBMARINE(3, "Submarine"),
    CRUISER(3, "Cruiser"),
    DESTROYER(2, "Destroyer"),
    NONE(0, null)
    ;

    public final int size;
    public final String name;

    Ship(int size, String name) {
        this.size = size;
        this.name = name;
    }
}
