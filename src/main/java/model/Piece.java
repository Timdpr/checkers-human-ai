package main.java.model;

/**
 * @author tp275
 */
public class Piece {

    private char colour;
    private boolean isKing;

    /**
     *
     * @param colour
     */
    public Piece(char colour) {
        this.colour = colour;
        this.isKing = false;
    }

    /**
     *
     * @param colour
     * @param isKing
     */
    public Piece(char colour, boolean isKing) {
        this.colour = colour;
        this.isKing = isKing;
    }

    /**
     *
     * @return
     */
    public char getColour() {
        return colour;
    }

    /**
     *
     * @return
     */
    public boolean isKing() {
        return isKing;
    }

    /**
     *
     * @param isKing
     */
    public void setKing(boolean isKing) {
        this.isKing = isKing;
    }
}
