package main.java.model;

import java.awt.Point;

/**
 * Contains helper methods for checking the validity of 'sliding' and 'jumping' moves.
 * @author tp275
 */
public class MoveValidator {

    /**
     * Checks whether the end location of the move is outside the board or blocked by a piece, and thus if it is valid.
     * Only use with 'sliding' moves!
     *
     * @param board the game board
     * @param end the location of the piece after the move
     * @return boolean of whether the slide move is valid
     */
    public boolean isSlideValid(Board board, Point end) {
        if (end.x > 7 || end.x < 0 || end.y > 7 || end.y < 0) {
            return false; // not valid if end location is outside board
        }
        if (board.getPiece(end.x, end.y) != null) {
            return false; // not valid if end location isn't empty
        }
        return true;
    }

    /**
     * Checks whether the end location of the move is outside the board or blocked by a piece, and whether there is a
     * correctly coloured intermediate piece to jump over.
     * Only use with 'jumping' moves!
     *
     * @param board the game board
     * @param inter the location of the square between the start and the end of the move
     * @param end the location of the piece after the move
     * @param colour the colour of the piece being moved
     * @return boolean of whether the jump move is valid
     */
    public boolean isJumpValid(Board board, Point inter, Point end, char colour) {
        if (!isSlideValid(board, end)) {
            return false; // not valid if end location is outside board or empty
        }
        Piece interPiece = board.getPiece(inter.x, inter.y);
        if (interPiece == null) {
            return false; // not valid if intermediate piece is missing
        }
        if (interPiece.getColour() == colour) {
            return false; // not valid if intermediate piece is the same colour
        }
        return true;
    }
}
