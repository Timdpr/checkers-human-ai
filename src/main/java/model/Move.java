package main.java.model;

import java.awt.Point;
import java.util.Objects;

/**
 *
 */
public class Move {

    protected Point origin;
    protected Point destination;
    protected Point pieceToRemove;
    protected boolean kingPiece;

    /**
     *
     * @param origin
     * @param destination
     */
    public Move(Point origin, Point destination) {
        this.origin = origin;
        this.destination = destination;
        this.pieceToRemove = null;
        this.kingPiece = false;
    }

    /**
     *
     * @param origin
     * @param destination
     * @param pieceToRemove
     */
    public Move(Point origin, Point destination, Point pieceToRemove) {
        this.origin = origin;
        this.destination = destination;
        this.pieceToRemove = pieceToRemove;
    }

    /**
     *
     * @return
     */
    public Point getOrigin() {
        return origin;
    }

    /**
     *
     * @return
     */
    public Point getDestination() {
        return destination;
    }

    /**
     *
     * @return
     */
    public Point getPieceToRemove() {
        return pieceToRemove;
    }

    /**
     *
     * @return
     */
    public boolean hasPieceToRemove() {
        return this.pieceToRemove != null;
    }

    /**
     *
     */
    public void setKingPiece() {
        this.kingPiece = true;
    }

    /**
     *
     * @param o
     * @return
     */
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

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(origin.x, origin.y, destination.x, destination.y);
    }
}
