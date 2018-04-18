package main.java.model;

import java.awt.*;

public class Move {

    protected Point origin;
    protected Point destination;
    protected Point pieceToRemove;

    public Move(Point origin, Point destination) {
        this.origin = origin;
        this.destination = destination;
        this.pieceToRemove = null;
    }

    public Move(Point origin, Point destination, Point pieceToRemove) {
        this.origin = origin;
        this.destination = destination;
        this.pieceToRemove = pieceToRemove;
    }

    public Point getOrigin() {
        return origin;
    }

    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    public Point getDestination() {
        return destination;
    }

    public void setDestination(Point destination) {
        this.destination = destination;
    }

    public Point getPieceToRemove() {
        return pieceToRemove;
    }

    public void setPieceToRemove(Point pieceToRemove) {
        this.pieceToRemove = pieceToRemove;
    }

    public boolean hasPieceToRemove() {
        return this.pieceToRemove != null;
    }
}
