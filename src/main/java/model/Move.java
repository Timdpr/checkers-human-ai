package main.java.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A Move object, with an origin, destination and optional 'piece to remove' (in a jump) as board-level Points.
 * Also has 'kingPiece' boolean which can be checked to see if the piece moved in the Move should become a king.
 *
 * @author tp275
 */
public class Move {

    final Point origin;
    final Point destination;
    final ArrayList<Point> piecesToRemove;
    boolean kingPiece;

    /**
     * Creates a new Move object with just an origin and destination Point
     * @param origin the origin Point of the move
     * @param destination the destination Point of the move
     */
    public Move(Point origin, Point destination) {
        this.origin = origin;
        this.destination = destination;
        this.piecesToRemove = null;
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
        this.piecesToRemove = new ArrayList<>();
        this.piecesToRemove.add(pieceToRemove);
    }

    public Move(Point origin, Point destination, ArrayList<Point> piecesToRemove) {
        this.origin = origin;
        this.destination = destination;
        this.piecesToRemove = piecesToRemove;
    }

    public Move(ArrayList<Move> previousMoves, Move newMove) {
        this.origin = new Point(previousMoves.get(0).origin.x, previousMoves.get(0).origin.y);
        this.destination = new Point(newMove.destination.x, newMove.destination.y);

        this.piecesToRemove = new ArrayList<>();
        for (int i = 0; i < previousMoves.size(); i++) {
            this.piecesToRemove.add(previousMoves.get(i).getPiecesToRemove().get(0));
        }
        piecesToRemove.add(newMove.getPiecesToRemove().get(0));
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
    public ArrayList<Point> getPiecesToRemove() {
        return piecesToRemove;
    }

    /**
     * Returns true if there is a 'piece to remove' set in the move (and therefore the move is a jump move!)
     * @return true if there is a 'piece to remove' set in the move (and therefore the move is a jump move!), else false
     */
    public boolean hasPieceToRemove() {
        return this.piecesToRemove != null;
    }

    /**
     * Sets the kingPiece boolean as true
     */
    public void setKingPiece() {
        this.kingPiece = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(origin, move.origin) &&
                Objects.equals(destination, move.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, destination);
    }

    @Override
    public String toString() {
        return origin.x + "," + origin.y + " - " + destination.x + "," + destination.y;
    }
}
