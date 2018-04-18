package main.java.model;

import java.awt.*;
import java.util.ArrayList;

public class MoveGenerator {

    private MoveCheck check = new MoveCheck();
    private ArrayList<Move> validMoves;

    /**
     *
     * @return
     */
    public ArrayList<Move> findValidMoves(Board board) {
        validMoves = new ArrayList<>();

        addValidJumps(board);
        if (validMoves.size() > 0) { // if there is a jump, it has to be made!
            return validMoves;
        }
        addValidSlides(board);

        return validMoves;
    }

    /**
     *
     * @return
     */
    private void addValidSlides(Board board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) { // for all board positions
                if (board.getPiece(i, j) != null) { // if there is a piece
                    Piece piece = board.getPiece(i, j);
                    if (piece.isKing()) {
                        check.isValid(board, new Point(i, j), new Point(i - 1, j - 1));
                        
                    }
                }
            }
        }
    }

    /**
     *
     * @return
     */
    private void addValidJumps(Board board) {

    }
}
