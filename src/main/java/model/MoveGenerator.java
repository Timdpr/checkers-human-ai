package main.java.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 *
 */
public class MoveGenerator {

    private MoveValidator check = new MoveValidator();
    private ArrayList<Move> validMoves;

    /**
     * Populates and returns an ArrayList<Move> with valid moves for all pieces of the given colour (note that if there
     * is one or more jump move they will be the only ones returned, as they have have to be made as per the rules)
     *
     * @return ArrayList of all valid Moves for all pieces of the given colour which are on the given Board
     */
    public ArrayList<Move> findValidMoves(Board board, char colour) {
        Board boardCopy = new Board(board.getBoard());
        validMoves = new ArrayList<>();
        addValidJumps(boardCopy, colour); // first find valid jumps
        if (validMoves.size() > 0) { // if there is a jump, it has to be made!
            return validMoves;
        }
        addValidSlides(boardCopy, colour); // otherwise, now find valid slide moves
        return validMoves;
    }

    /**
     * Populates validMoves with all valid slide moves for pieces of the given colour
     *
     * @param board the internal board state
     * @param colour the colour of pieces to generate moves for ('r' or 'w')
     */
    private void addValidSlides(Board board, char colour) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) { // for all board positions
                if (board.getPiece(i, j) != null) { // if there is a piece
                    Piece piece = board.getPiece(i, j);
                    Point origin = new Point(i, j);

                    // go through downwards diagonal moves for white or kings
                    if ((piece.getColour() == 'w' && piece.getColour() == colour)
                            || (piece.isKing() && piece.getColour() == colour)) {
                        Point downLeft = new Point(i+1, j-1);
                        if (check.isSlideValid(board, downLeft)) {
                            validMoves.add(new Move(origin, downLeft));
                        }
                        Point downRight = new Point(i+1, j+1);
                        if (check.isSlideValid(board, downRight)) {
                            validMoves.add(new Move(origin, downRight));
                        }
                    }
                    // go through upwards diagonal moves for red or kings
                    if ((piece.getColour() == 'r' && piece.getColour() == colour)
                            || (piece.isKing() && piece.getColour() == colour)) {
                        Point upLeft = new Point(i-1, j-1);
                        if (check.isSlideValid(board, upLeft)) {
                            validMoves.add(new Move(origin, upLeft));
                        }
                        Point upRight = new Point(i-1, j+1);
                        if (check.isSlideValid(board, upRight)) {
                            validMoves.add(new Move(origin, upRight));
                        }
                    }
                }
            }
        }
    }

    /**
     * Populates validMoves with all valid jump moves for pieces of the given colour
     *
     * @param board the internal board state
     * @param colour the colour of pieces to generate moves for ('r' or 'w')
     */
    private void addValidJumps(Board board, char colour) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) { // for all board positions
                if (board.getPiece(i, j) != null) { // if there is a piece
                    Piece piece = board.getPiece(i, j);
                    Point origin = new Point(i, j);
                    char pieceColour = piece.getColour();

                    // go through downwards diagonal jumps for white or kings
                    if ((piece.getColour() == 'w' && piece.getColour() == colour)
                            || (piece.isKing() && piece.getColour() == colour)) {
                        Point downLeft = new Point(i+1, j-1);
                        Point down2Left2 = new Point(i+2, j-2);
                        if (check.isJumpValid(board, downLeft, down2Left2, pieceColour)) {
                            validMoves.add(new Move(origin, down2Left2, downLeft));
                        }
                        Point downRight = new Point(i+1, j+1);
                        Point down2Right2 = new Point(i+2, j+2);
                        if (check.isJumpValid(board, downRight, down2Right2, pieceColour)) {
                            validMoves.add(new Move(origin, down2Right2, downRight));
                        }
                    }
                    // go through upwards diagonal jumps for red or kings
                    if ((piece.getColour() == 'r' && piece.getColour() == colour)
                            || (piece.isKing() && piece.getColour() == colour)) {
                        Point upLeft = new Point(i-1, j-1);
                        Point up2Left2 = new Point(i-2, j-2);
                        if (check.isJumpValid(board, upLeft, up2Left2, pieceColour)) {
                            validMoves.add(new Move(origin, up2Left2, upLeft));
                        }
                        Point upRight = new Point(i-1, j+1);
                        Point up2Right2 = new Point(i-2, j+2);
                        if (check.isJumpValid(board, upRight, up2Right2, pieceColour)) {
                            validMoves.add(new Move(origin, up2Right2, upRight));
                        }
                    }
                }
            }
        }
    }

    public boolean detectMultiMove(Board board, char color, Point destination) {
        validMoves.clear();
        addValidJumps(board, color);

        if (validMoves.size() > 0) {
            System.out.println("Destination of last move: " + destination);
            System.out.println(validMoves.toString());
            validMoves.removeIf(e -> (e.getOrigin().x != destination.x || e.getOrigin().y != destination.y));
            System.out.println(validMoves.toString());
            if (validMoves.size() > 0) {
                validMoves.clear();
                return true;
            }
        }
        validMoves.clear();
        return false;
    }
}
