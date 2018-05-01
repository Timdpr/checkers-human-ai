package main.java.model;

import java.awt.*;
import java.util.ArrayList;

/**
 *
 */
public class MoveGenerator {

    private MoveValidator check = new MoveValidator();

    /**
     * Populates and returns an ArrayList<Move> with valid moves for all pieces of the given colour (note that if there
     * is one or more jump move they will be the only ones returned, as they have have to be made as per the rules)
     *
     * @return ArrayList of all valid Moves for all pieces of the given colour which are on the given Board
     */
    public ArrayList<Move> findValidMoves(Board board, char colour) {
        Board boardCopy = new Board(board.getBoard());
        ArrayList<Move> validMoves = new ArrayList<>(getValidJumps(boardCopy, colour)); // first find valid jumps
        if (validMoves.size() > 0) { // if there is a jump, it has to be made!
            updateKings(validMoves, colour);
            return validMoves;
        }
        validMoves.addAll(getValidSlides(boardCopy, colour)); // otherwise, now find valid slide moves
        updateKings(validMoves, colour);
        return validMoves;
    }

    private ArrayList<Move> updateKings(ArrayList<Move> validMoves, char colour) {
        for (Move m : validMoves) {
            if (m.destination.x == 0 && colour=='r') {
                m.setKingPiece();
            } else if (m.destination.x == 7 && colour=='w') {
                m.setKingPiece();
            }
        }
        return validMoves;
    }

    /**
     * Populates validMoves with all valid slide moves for pieces of the given colour
     *
     * @param board the internal board state
     * @param colour the colour of pieces to generate moves for ('r' or 'w')
     */
    private ArrayList<Move> getValidSlides(Board board, char colour) {
        ArrayList<Move> validSlides = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = (i+1)%2; j < 8; j+=2) { // for all board positions
                if (board.getPiece(i, j) != null) { // if there is a piece
                    Piece piece = board.getPiece(i, j);
                    Point origin = new Point(i, j);

                    // go through downwards diagonal moves for white or kings
                    if ((colour == 'w' && piece.getColour() == colour)
                            || (piece.isKing() && piece.getColour() == colour)) {
                        Point downLeft = new Point(i+1, j-1);
                        if (check.isSlideValid(board, downLeft)) {
                            validSlides.add(new Move(origin, downLeft));
                        }
                        Point downRight = new Point(i+1, j+1);
                        if (check.isSlideValid(board, downRight)) {
                            validSlides.add(new Move(origin, downRight));
                        }
                    }
                    // go through upwards diagonal moves for red or kings
                    if ((colour == 'r' && piece.getColour() == colour)
                            || (piece.isKing() && piece.getColour() == colour)) {
                        Point upLeft = new Point(i-1, j-1);
                        if (check.isSlideValid(board, upLeft)) {
                            validSlides.add(new Move(origin, upLeft));
                        }
                        Point upRight = new Point(i-1, j+1);
                        if (check.isSlideValid(board, upRight)) {
                            validSlides.add(new Move(origin, upRight));
                        }
                    }
                }
            }
        } return validSlides;
    }

    /**
     * Populates validMoves with all valid jump moves for pieces of the given colour
     *
     * @param board the internal board state
     * @param colour the colour of pieces to generate moves for ('r' or 'w')
     */
    private ArrayList<Move> getValidJumps(Board board, char colour) {
        ArrayList<Move> validJumps = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = (i+1)%2; j < 8; j+=2) { // for all board positions
                if (board.getPiece(i, j) != null) { // if there is a piece
                    Piece piece = board.getPiece(i, j);
                    Point origin = new Point(i, j);

                    // go through downwards diagonal jumps for white or kings
                    if ((colour == 'w' && piece.getColour() == colour)
                            || (piece.isKing() && piece.getColour() == colour)) {
                        Point downLeft = new Point(i + 1, j - 1);
                        Point down2Left2 = new Point(i + 2, j - 2);
                        if (check.isJumpValid(board, downLeft, down2Left2, colour)) {
                            validJumps.add(new Move(origin, down2Left2, downLeft));
                        }
                        Point downRight = new Point(i + 1, j + 1);
                        Point down2Right2 = new Point(i + 2, j + 2);
                        if (check.isJumpValid(board, downRight, down2Right2, colour)) {
                            validJumps.add(new Move(origin, down2Right2, downRight));
                        }
                    }
                    // go through upwards diagonal jumps for red or kings
                    if ((colour == 'r' && piece.getColour() == colour)
                            || (piece.isKing() && piece.getColour() == colour)) {
                        Point upLeft = new Point(i - 1, j - 1);
                        Point up2Left2 = new Point(i - 2, j - 2);
                        if (check.isJumpValid(board, upLeft, up2Left2, colour)) {
                            validJumps.add(new Move(origin, up2Left2, upLeft));
                        }
                        Point upRight = new Point(i - 1, j + 1);
                        Point up2Right2 = new Point(i - 2, j + 2);
                        if (check.isJumpValid(board, upRight, up2Right2, colour)) {
                            validJumps.add(new Move(origin, up2Right2, upRight));
                        }
                    }
                }
            }
        } return validJumps;
    }

    /**
     * Determines if the given player can make another jump in their turn (as per the multi-step rules)
     * @param board the board state after the last move
     * @param color the colour of the pieces to check
     * @param destination the current location of the piece to check (ie. the destination of the previous move)
     * @return true if there is another jump available for the piece in the player's previous move, else false
     */
    public ArrayList<Move> detectMultiMove(Board board, char color, Point destination) {
        ArrayList<Move> vJumps = getValidJumps(board, color);
        if (vJumps.size() > 0) { // if there are valid jumps on the board for the player
            // Remove all jumps that don't start at the destination of the last move
            vJumps.removeIf(e -> (e.getOrigin().x != destination.x || e.getOrigin().y != destination.y));
            if (vJumps.size() > 0) {
                return vJumps;
            } // and return true if there is still an available jump
        }
        return vJumps;
    }
}
