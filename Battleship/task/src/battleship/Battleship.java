package battleship;

import java.util.*;
import java.util.stream.IntStream;

public class Battleship {

    public static final int SIZE = 10;
    private final Scanner scanner;
    private boolean running = true;
    private boolean fogOfWar = false;
    private final Player[] players;

    public Battleship(int nbPlayers) {
        scanner = new Scanner(System.in);
        players = new Player[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            players[i] = new Player("Player " + (i + 1));
        }
        this.fillPlayersGrid();
    }

    private void bePolite() {
        System.out.println("Press Enter and pass the move to another player");
        scanner.nextLine();
        System.out.println("...");
    }

    public void run() {
        for (Player player : players) {
            addShips(player);
            bePolite();
        }
        start();
        end();
    }

    public void start() {
        fogOfWar = true;
        System.out.println("The game starts!");
        do {
            for (int i = 0; i < players.length; i++) {
                this.displayPlayersGrid(players[i]);
                System.out.println(players[i].name + ", it's your turn:");
                while (!takeAShot(scanner.nextLine(), players[i == 0? 1 : 0]));
                if (isAllShipsSank(players[i == 0? 1 : 0])) {
                    System.out.println("You sank the last ship. " + players[i].name + " won. Congratulations!");
                    running = false;
                }
                bePolite();
            }
        } while(running);
    }

    public void end() {
        scanner.close();
    }

    public void fillPlayersGrid() {
        for (Player player : players) {
            for (char[] row : player.grid) {
                Arrays.fill(row, Indicator.WATER.value);
            }
        }
    }

    public void displayPlayersGrid(Player actualPlayer) {
        System.out.println("\n  ");
        Player top = actualPlayer == players[0] ? players[1] : players[0];
        Player bottom = actualPlayer == players[0] ? players[0] : players[1];
        displayGrid(top);
        System.out.println("---------------------");
        fogOfWar = false;
        displayGrid(bottom);
        fogOfWar = true;
        System.out.println();
    }

    private void displayGrid(Player player) {
        for (int i = 1; i <= player.grid.length; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        for (int i = 0, fieldsLength = player.grid.length; i < fieldsLength; i++) {
            System.out.print((char)('A' + i) + " ");
            char[] field = player.grid[i];
            for (char cell : field) {
                System.out.print((fogOfWar && cell == Indicator.TARGET.value ? "~" : cell) + " ");
            }
            System.out.println();
        }
    }

    public void addShips(Player player) {
        System.out.println(player.name + ", place your ship on the game field\n");
        displayGrid(player);
        for (Ship ship : Ship.values()) {
            if (ship.equals(Ship.NONE)) return;
            System.out.println("Enter the coordinates of the " + ship.name + " ("+ ship.size + " cells):");
            String[] val;
            do { val = scanner.nextLine().split(" "); }
            while(tryAddShip(val[0], val[1], ship, player));
        }
    }

    private boolean tryAddShip(String startCell, String endCell, Ship ship, Player player) {
        int[] start = parseInput(startCell);
        int[] end = parseInput(endCell);
        if (start == null || end == null) return true;
        Coordinate coordinate = new Coordinate(start[0], start[1], end[0], end[1]);
        if (!isRightLocation(coordinate)) {
            System.out.println("Error! Wrong ship location! Try again:");
            return true;
        } else if (!isMatchLength(coordinate, ship)) {
            System.out.println("Error! Wrong length of the " + ship.name + "! Try again:");
            return true;
        } else if (isNearToOtherShips(coordinate, player)) {
            System.out.println("Error! You placed it too close to another one. Try again:");
            return true;
        }
        insertShipOnGrid(coordinate, ship, player);
        this.displayGrid(player);
        return false;
    }

    private void insertShipOnGrid(Coordinate coordinate, Ship ship, Player player) {
        player.ships.put(ship, coordinate);
        int sign = getSign(coordinate);
        for (int i = coordinate.sx(); i*sign <= coordinate.ex()*sign; i+=sign) {
            for (int j = coordinate.sy(); j*sign <= coordinate.ey()*sign; j+=sign) {
                player.grid[i][j] = Indicator.TARGET.value;
            }
        }
    }

    private boolean isMatchLength(Coordinate c, Ship ship) {
        return (c.sx() - c.ex() == 0 ? Math.abs(c.sy() - c.ey()) : Math.abs(c.sx() - c.ex())) + 1 == ship.size;
    }

    private boolean isRightLocation(Coordinate c) {
        return Math.abs(c.sx() - c.ex()) == 0 || Math.abs(c.sy() - c.ey()) == 0;
    }

    private boolean isNearToOtherShips(Coordinate c, Player player) {
        int sign = c.ex() - c.sx() == 0 ? c.ey() - c.sy() : c.ex() - c.sx();
        sign /= Math.abs(sign);
        for (int i = c.sx() - sign; i * sign < (c.ex() + sign) * sign; i+=sign) {
            for (int j = c.sy() - sign; i != c.sx() && i != c.ex() && i >= 0 && i < player.grid.length && j * sign < (c.ey() + sign) * sign; j+=sign) {
                if (j >= 0 && j < player.grid.length && j != c.sy() && j != c.ey() && player.grid[i][j] == Indicator.TARGET.value) return true;
            }
        }
        return false;
    }

    private int[] parseInput(String coordinate) {
        return coordinate.isEmpty() ? null : new int[] {coordinate.charAt(0) - 'A', Integer.parseInt(coordinate.substring(1)) - 1};
    }

    private boolean takeAShot(String target, Player player) {
        int[] cell = parseInput(target);
        if (cell == null) return false;
        if (cell[0] < 0 || cell[0] >= player.grid.length || cell[1] < 0 || cell[1] >= player.grid.length) {
            System.out.println("Error! You entered the wrong coordinates! Try again:");
            return false;
        }
        if (player.grid[cell[0]][cell[1]] == Indicator.TARGET.value) {
            player.grid[cell[0]][cell[1]] = Indicator.STRIKE.value;
            if (tryToRemoveShip(player)) System.out.println("You sank a ship!");
            else System.out.println("You hit a ship!");
        } else {
            player.grid[cell[0]][cell[1]] = player.grid[cell[0]][cell[1]] == Indicator.STRIKE.value ? player.grid[cell[0]][cell[1]] : Indicator.MISSED.value;
            System.out.println("You missed!");
        }
        return true;
    }

    private boolean isAllShipsSank(Player player) {
        return player.ships.keySet().stream().allMatch(ship -> isOneShipSank(player.ships.get(ship), player));
    }

    private boolean isOneShipSank(Coordinate c, Player player) {
        int sign = getSign(c);
        return IntStream.iterate(c.sx(), i -> i * sign <= c.ex() * sign, i -> i + sign)
                .noneMatch(i -> IntStream.iterate(c.sy(), j -> j * sign <= c.ey() * sign, j -> j + sign)
                        .anyMatch(j -> player.grid[i][j] == Indicator.TARGET.value));
    }

    private boolean tryToRemoveShip(Player player) {
        Ship shipSank = player.ships.keySet().stream().filter(ship -> isOneShipSank(player.ships.get(ship), player)).findFirst().orElse(Ship.NONE);
        if (!shipSank.equals(Ship.NONE)) player.ships.remove(shipSank);
        return !shipSank.equals(Ship.NONE);
    }

    private int getSign(Coordinate c) {
        int sign = c.ex() - c.sx() == 0 ? c.ey() - c.sy() : c.ex() - c.sx();
        return sign / Math.abs(sign);
    }
}
