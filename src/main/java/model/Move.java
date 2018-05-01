package main.java.model;

import java.awt.*;
import java.util.Objects;

public class Move {

    protected Point origin;
    protected Point destination;
    protected Point pieceToRemove;
    protected boolean kingPiece;

    public Move(Point origin, Point destination) {
        this.origin = origin;
        this.destination = destination;
        this.pieceToRemove = null;
        this.kingPiece = false;
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

    public Point getPieceToRemove() {
        return pieceToRemove;
    }

    public boolean hasPieceToRemove() {
        return this.pieceToRemove != null;
    }

    public void setKingPiece() {
        this.kingPiece = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(origin.x, move.origin.x) &&
                Objects.equals(destination.x, move.destination.x) &&
                Objects.equals(origin.y, move.origin.y) &&
                Objects.equals(destination.y, move.destination.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin.x, origin.y, destination.x, destination.y);
    }

    @Override
    public String toString() {
        return "Move{" +
                "origin=" + origin +
                ", destination=" + destination +
                ", pieceToRemove=" + pieceToRemove +
                '}';
    }
}
