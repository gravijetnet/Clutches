package net.gravijet.clutches.game;

/** Richtung, aus der der simulierte Treffer kommt. */
public enum Direction {

    FRONT("Vorne"),
    BACK("Hinten"),
    LEFT("Links"),
    RIGHT("Rechts"),
    RANDOM("Zufällig");

    private final String display;

    Direction(String display) {
        this.display = display;
    }

    public String display() {
        return display;
    }

    public Direction next() {
        Direction[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public Direction previous() {
        Direction[] values = values();
        return values[(ordinal() - 1 + values.length) % values.length];
    }
}
