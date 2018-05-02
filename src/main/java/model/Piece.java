package main.java.model;

/**
 * A Piece object used in the model board. Simply has a colour, as a 'r' or 'w' char, and a boolean to determine
 * whether it is a King or not.
 *
 * @author tp275
 */
public class Piece {

    private char colour;
    private boolean isKing;

    /**
     * Creates a new piece with given colour. isKing = false by default.
     * @param colour the colour to set the piece as
     */
    public Piece(char colour) {
        this.colour = colour;
        this.isKing = false;
    }

    /**
     * Creates a new piece with given colour and isKing boolean
     * @param colour the colour to set the piece as
     * @param isKing sets whether the Piece is a king or not
     */
    public Piece(char colour, boolean isKing) {
        this.colour = colour;
        this.isKing = isKing;
    }

    /**
     * Returns the piece's colour
     * @return the piece's colour as a char
     */
    public char getColour() {
        return colour;
    }

    /**
     * Returns whether the piece is a king
     * @return true if king, else false
     */
    public boolean isKing() {
        return isKing;
    }

    /**
     * Sets the status of isKing
     * @param isKing true to set piece as a king, and vice-versa
     */
    public void setKing(boolean isKing) {
        this.isKing = isKing;
    }
}
