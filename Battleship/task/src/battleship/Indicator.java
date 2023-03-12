package battleship;

public enum Indicator {
    STRIKE('X'),
    WATER('~'),
    TARGET('O'),
    MISSED('M')
    ;

    public char value;

    Indicator(char x) {
        this.value = x;
    }
}
