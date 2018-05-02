package main.java.model;

import java.awt.Point;
import java.util.Objects;

/**
 * A Move object, with an origin, destination and optional 'piece to remove' (in a jump) as board-level Points.
 * Also has 'kingPiece' boolean which can be checked to see if the piece moved in the Move should become a king.
 */
public class Move {

    protected Point origin;
    protected Point destination;
    protected Point pieceToRemove;
    protected boolean kingPiece;

    /**
     * Creates a new Move object with just an origin and destination Point
     * @param origin the origin Point of the move
     * @param destination the destination Point of the move
     */
    public Move(Point origin, Point destination) {
        this.origin = origin;
        this.destination = destination;
        this.pieceToRemove = null;
        this.kingPiece = false;
    }

    /**
     * Creates a new Move object with an origin, destination and a 'piece to remove' location. This will be a jump.
     * @param origin the origin Point of the move
     * @param destination the destination Point of the move
     * @param pieceToRemove the Point location of the piece to remove
     */
    public Move(Point origin, Point destination, Point pieceToRemove) {
        this.origin = origin;
        this.destination = destination;
        this.pieceToRemove = pieceToRemove;
    }

    /**
     * Returns the origin Point of the move
     * @return the origin Point of the move
     */
    public Point getOrigin() {
        return origin;
    }

    /**
     * Returns the destination Point of the move
     * @return the destination Point of the move
     */
    public Point getDestination() {
        return destination;
    }

    /**
     * Returns the 'piece to remove' Point of the move
     * @return the 'piece to remove' Point of the move
     */
    public Point getPieceToRemove() {
        return pieceToRemove;
    }

    /**
     * Returns true if there is a 'piece to remove' set in the move (and therefore the move is a jump move!)
     * @return true if there is a 'piece to remove' set in the move (and therefore the move is a jump move!), else false
     */
    public boolean hasPieceToRemove() {
        return this.pieceToRemove != null;
    }

    /**
     * Sets the kingPiece boolean as true
     */
    public void setKingPiece() {
        this.kingPiece = true;
    }

    /**
     * @return whether given object has the same origin and destination as this one
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
     * @return the object's hashcode, using origin and destination
     */
    @Override
    public int hashCode() {
        return Objects.hash(origin.x, origin.y, destination.x, destination.y);
    }
}
