package main.java;

public class Piece {

    private char colour;
    private boolean isKing;

    public Piece(char colour) {
        this.colour = colour;
        this.isKing = false;
    }

    public char getColour() {
        return colour;
    }

    public void setColour(char colour) {
        this.colour = colour;
    }

    public boolean isKing() {
        return isKing;
    }

    public void setKing(boolean king) {
        isKing = king;
    }

}
