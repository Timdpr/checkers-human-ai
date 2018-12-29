package main.java.model;

/**
 * A Piece object used in the model board. Simply has a colour, as a 'r' or 'w' char, and a boolean to determine
 * whether it is a King or not.
 *
 * @author tp275
 */
class Piece {

    private final char colour;
    private boolean isKing;

    /**
     * Creates a new piece with given colour. isKing = false by default.
     * @param colour the colour to set the piece as
     */
    Piece(char colour) {
        this.colour = colour;
        this.isKing = false;
    }

    /**
     * Creates a new piece with given colour and isKing boolean
     * @param colour the colour to set the piece as
     * @param isKing sets whether the Piece is a king or not
     */
    Piece(char colour, boolean isKing) {
        this.colour = colour;
        this.isKing = isKing;
    }

    /**
     * Returns the piece's colour
     * @return the piece's colour as a char
     */
    char getColour() {
        return colour;
    }

    /**
     * Returns whether the piece is a king
     * @return true if king, else false
     */
    boolean isKing() {
        return isKing;
    }

    /**
     * Sets isKing to true
     */
    void setKing() {
        this.isKing = true;
    }
}
