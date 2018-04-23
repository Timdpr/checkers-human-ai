package main.java.model;

import java.util.ArrayList;

/**
 * The state representation: a 2d array holding Piece objects.
 *
 * @author tp275
 */
public class Board {

    private Piece[][] board;

    /**
     * Creates and sets up pieces on the board
     */
    public Board() {
        this.board = getInitialBoard();
    }

    /**
     * Returns board array with pieces in their initial positions
     * @return Piece[][] with pieces in their initial positions
     */
    public Piece[][] getInitialBoard() {
        Piece[][] newBoard = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            if (i % 2 == 0) {
                newBoard[1][i] = new Piece('w', 1, i);
                newBoard[5][i] = new Piece('r', 5, i);
                newBoard[7][i] = new Piece('r', 7, i);
            } else {
                newBoard[0][i] = new Piece('w', 0, i);
                newBoard[2][i] = new Piece('w', 2, i);
                newBoard[6][i] = new Piece('r', 6, i);
            }
        }
        return newBoard;
    }

    /**
     * @return the current board, as Piece[][]
     */
    public Piece[][] getBoard() {
        return board;
    }

    /**
     *
     * @param row
     * @param col
     * @return
     */
    public Piece getPiece(int row, int col) {
        return board[row][col];
    }

    /**
     *
     * @param move
     */
    public void updateLocation(Move move) {
        // Store piece at origin and delete it from board
        Piece originPiece = board[move.origin.x][move.origin.y];
        board[move.origin.x][move.origin.y] = null;

        // If there is an intermediate piece (in a jump), remove it
        if (move.hasPieceToRemove()) {
            board[move.pieceToRemove.x][move.pieceToRemove.y] = null;
        }
        // Now insert the original piece at the destination
        board[move.destination.x][move.destination.y] = originPiece;
        updateKings();
    }

    /**
     * Prints a text representation of the current board
     */
    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    System.out.print(board[i][j].getColour() + " ");
                } else {
                    System.out.print("- ");
                }
            }
            System.out.println();
        }
    }

    public int winCheck() {
        int whites = 0;
        int reds = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    if (board[i][j].getColour() == 'w') {
                        whites += 1;
                    } else {
                        reds += 1;
                    }
        }}}
        if (whites == 0) {
            return 1;
        } else if (reds == 0) {
            return -1;
        }
        return 0;
    }

    public void updateKings() {
        for (Piece p : board[0]) {
            if (p!=null) {
                if (p.getColour()=='r' && !p.isKing()) {
                    p.setKing();
                }
            }
        }
        for (Piece p : board[7]) {
            if (p!=null) {
                if (p.getColour() == 'w' && !p.isKing()) {
                    p.setKing();
                }
            }
        }
    }
}
