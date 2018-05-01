package main.java.model;

/**
 * @author tp275
 */
public class Piece {

    private char colour;
    private boolean isKing;

    public Piece(char colour) {
        this.colour = colour;
        this.isKing = false;
    }

    public Piece(char colour, boolean isKing) {
        this.colour = colour;
        this.isKing = isKing;
    }

    public char getColour() {
        return colour;
    }

    public boolean isKing() {
        return isKing;
    }

    public void setKing(boolean isKing) {
        this.isKing = isKing;
    }
}
