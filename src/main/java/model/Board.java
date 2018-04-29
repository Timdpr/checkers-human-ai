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
     * Creates a new Board object with it's board as the given Piece[][] - useful for copying
     * @param board the state representation for the board to hold
     */
    public Board(Piece[][] board) {
        this.board = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, this.board[i], 0, 8);
        }
    }

    /**
     * Returns board array with pieces in their initial positions
     * @return Piece[][] with pieces in their initial positions
     */
    private Piece[][] getInitialBoard() {
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
     * TODO: Make this method update and return a copy of the updated board
     * @param move
     */
    public Board updateLocation(Move move) {
        Piece[][] boardCopy = this.board.clone();
        // Store piece at origin and delete it from board
        Piece originPiece = boardCopy[move.origin.x][move.origin.y];
        boardCopy[move.origin.x][move.origin.y] = null;

        // If there is an intermediate piece (in a jump), update piece counts then remove it
        if (move.hasPieceToRemove()) {
            boardCopy[move.pieceToRemove.x][move.pieceToRemove.y] = null;
        }
        // Now insert the original piece at the destination
        boardCopy[move.destination.x][move.destination.y] = originPiece;
        boardCopy = updateKings(boardCopy);
        return new Board(boardCopy);
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
        int whitePieces = 0;
        int redPieces = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) {
                if (this.board[i][j] != null) {
                    if (this.board[i][j].getColour() == 'r') {
                        redPieces++;
                    } else {
                        whitePieces++;
                    }
                }
            }
        }
        if (whitePieces == 0) {
            return 1;
        } else if (redPieces == 0) {
            return -1;
        }
        return 0;
    }

    private Piece[][] updateKings(Piece[][] boardCopy) {
        for (Piece p : boardCopy[0]) {
            if (p!=null) {
                if (p.getColour()=='r' && !p.isKing()) {
                    p.setKing();
                }
            }
        }
        for (Piece p : boardCopy[7]) {
            if (p!=null) {
                if (p.getColour() == 'w' && !p.isKing()) {
                    p.setKing();
                }
            }
        }
        return boardCopy;
    }
}
