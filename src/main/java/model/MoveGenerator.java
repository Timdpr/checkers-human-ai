package main.java.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * This class can be used to generate all valid moves for a given player and board state, as well as detecting whether
 * there is a multi-move available
 *
 * @author tp275
 */
public class MoveGenerator {

    private final MoveValidator check = new MoveValidator();

    /**
     * Populates and returns an ArrayList with valid moves for all pieces of the given colour (note that if there
     * is one or more jump move they will be the only ones returned, as they have have to be made as per the rules)
     *
     * @param board  the board state to find valid moves on
     * @param colour the player to find valid moves for
     * @return ArrayList of all valid Moves for all pieces of the given colour which are on the given Board
     */
    public ArrayList<Move> findValidMoves(Board board, char colour) {
        ArrayList<Move> validMoves = new ArrayList<>(getValidJumps(board, colour)); // first find valid jumps
        if (validMoves.size() > 0) { // if there is a jump, it has to be made!
            updateKings(validMoves, colour);
            return validMoves;
        }
        validMoves.addAll(getValidSlides(board, colour)); // otherwise, now find valid slide moves
        updateKings(validMoves, colour);
        return validMoves;
    }

    /**
     * Kings are generated here using valid move lists and a given colour, and setting 'kingPiece' in
     * the Moves, which can later be updated. This is more optimised than checking the board itself!
     *
     * @param validMoves a list of moves to check and update
     * @param colour     the colour of the pieces in the moves
     */
    private void updateKings(ArrayList<Move> validMoves, char colour) {
        for (Move m : validMoves) {
            if (m.destination.x == 0 && colour == 'r') {
                m.setKingPiece();
            } else if (m.destination.x == 7 && colour == 'w') {
                m.setKingPiece();
            }
        }
    }

    /**
     * Returns move list with all valid slide moves for pieces of the given colour
     *
     * @param board  the internal board state
     * @param colour the colour of pieces to generate moves for ('r' or 'w')
     * @return move list with all valid slide moves for pieces of the given colour
     */
    private ArrayList<Move> getValidSlides(Board board, char colour) {
        ArrayList<Move> validSlides = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) { // for all board positions
                if (board.getPiece(i, j) != null) { // if there is a piece
                    Piece piece = board.getPiece(i, j);
                    Point origin = new Point(i, j);

                    // go through downwards diagonal moves for white or kings
                    if ((colour == 'w' && piece.getColour() == colour)
                            || (piece.isKing() && piece.getColour() == colour)) {
                        Point downLeft = new Point(i + 1, j - 1);
                        if (check.isSlideValid(board, downLeft)) {
                            validSlides.add(new Move(origin, downLeft));
                        }
                        Point downRight = new Point(i + 1, j + 1);
                        if (check.isSlideValid(board, downRight)) {
                            validSlides.add(new Move(origin, downRight));
                        }
                    }
                    // go through upwards diagonal moves for red or kings
                    if ((colour == 'r' && piece.getColour() == colour)
                            || (piece.isKing() && piece.getColour() == colour)) {
                        Point upLeft = new Point(i - 1, j - 1);
                        if (check.isSlideValid(board, upLeft)) {
                            validSlides.add(new Move(origin, upLeft));
                        }
                        Point upRight = new Point(i - 1, j + 1);
                        if (check.isSlideValid(board, upRight)) {
                            validSlides.add(new Move(origin, upRight));
                        }
                    }
                }
            }
        }
        return validSlides;
    }

    /**
     * Returns move list with all valid jump moves for pieces of the given colour
     *
     * @param board  the internal board state
     * @param colour the colour of pieces to generate moves for ('r' or 'w')
     * @return move list with all valid slide moves for pieces of the given colour
     */
    private ArrayList<Move> getValidJumps(Board board, char colour) {
        ArrayList<Move> validJumps = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) { // for all board positions
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
        }
        return validJumps;
    }

    /**
     * Determines if the given player can make another jump in their turn (as per the multi-step rules)
     *
     * @param board       the board state after the last move
     * @param color       the colour of the pieces to check
     * @param destination the current location of the piece to check (ie. the destination of the previous move)
     * @return true if there is another jump available for the piece in the player's previous move, else false
     */
    public ArrayList<Move> detectMultiMove(Board board, char color, Point destination) {
        ArrayList<Move> validJumps = getValidJumps(board, color);
        if (validJumps.size() > 0) { // if there are valid jumps on the board for the player
            // Remove all jumps that don't start at the destination of the last move
            validJumps.removeIf(e -> (e.getOrigin().x != destination.x || e.getOrigin().y != destination.y));
            if (validJumps.size() > 0) {
                return validJumps;
            } // and return true if there is still an available jump
        }
        return validJumps;
    }

    public ArrayList<Move> updateValidMovesWithJumps(Board board, ArrayList<Move> children, char color) {
        ArrayList<Move> movesToAdd = new ArrayList<>();

        for (Move move : children) {
            if (move.hasPieceToRemove()) { // for each jump move in the original list of child moves
                ArrayList<Move> validJumps = getValidJumpsFromMove(board, color, move, move.kingPiece);
                if (validJumps.size() > 0) { // if there is another jump move

                    ArrayList<Move> previousMoves = new ArrayList<>();
                    previousMoves.add(move);
                    testUpdateLoop(board, color, move, previousMoves, validJumps, movesToAdd);

                } else if (!movesToAdd.contains(move)) { // if a jump move in the original list didn't have any more jumps
                    movesToAdd.add(move);
                }
            } else if (!movesToAdd.contains(move)) { // if a move in the original list wasn't a jump
                movesToAdd.add(move);
            }
        }
        updateKings(movesToAdd, color);
        return movesToAdd;
    }

    private void testUpdateLoop(Board board, char color, Move originalMove, ArrayList<Move> previousMoves, ArrayList<Move> validJumps, ArrayList<Move> movesToAdd) {
        for (Move furtherJump : validJumps) {
            if (originalMove.hasPieceToRemove()) {
                ArrayList<Move> validFurtherJumps = getValidJumpsFromMove(board, color, previousMoves, furtherJump);
                if (validFurtherJumps.size() > 0) {
                    previousMoves.add(furtherJump);
                    testUpdateLoop(board, color, originalMove, previousMoves, validFurtherJumps, movesToAdd);
                } else if (!movesToAdd.contains(furtherJump)) { // if a further jump move didn't have any more jumps
                    movesToAdd.add(new Move(previousMoves, furtherJump));
                }
            } else if (!movesToAdd.contains(furtherJump)) { // if a further jump move didn't have any more jumps
                movesToAdd.add(new Move(previousMoves, furtherJump));
            }
        }
    }

    private ArrayList<Move> getValidJumpsFromMove(Board board, char color, Move move, boolean king) {
        ArrayList<Move> validJumps = getValidJumps(board.updateLocation(move), color); // get jump from that move's resulting location
        validJumps.removeIf(e -> (e.getOrigin().x != move.destination.x || e.getOrigin().y != move.destination.y));
        if (king) {
            for (Move m : validJumps) {
                m.setKingPiece();
            }
        }
        return validJumps;
    }

    private ArrayList<Move> getValidJumpsFromMove(Board board, char color, ArrayList<Move> previousMoves, Move move) {
        Board updatedBoard = new Board(board.getBoard());
        boolean previousMoveKing = false;
        for (Move previousMove : previousMoves) {
            if (move.kingPiece) {
                previousMoveKing = true;
            }
            previousMove.setKingPiece();
            updatedBoard = updatedBoard.updateLocation(previousMove);
        }
        return getValidJumpsFromMove(updatedBoard, color, move, previousMoveKing);
    }
}
