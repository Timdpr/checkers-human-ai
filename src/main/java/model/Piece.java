package main.java.model;

import java.awt.Point;

/**
 * @author tp275
 */
public class Piece {

    private char colour;
    private boolean isKing;
    private Point position;

    public Piece(char colour, int row, int col) {
        this.colour = colour;
        this.position = new Point(row, col);
        this.isKing = false;
    }

    public char getColour() {
        return colour;
    }

    public void setColour(char colour) {
        this.colour = colour;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public boolean isKing() {
        return isKing;
    }

    public void setKing() {
        isKing = true;
    }
}
