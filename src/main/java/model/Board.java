package main.java.model;

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
                newBoard[5][i] = new Piece('w', 5, i);
                newBoard[7][i] = new Piece('w', 7, i);
            } else {
                newBoard[0][i] = new Piece('w', 0, i);
                newBoard[2][i] = new Piece('w', 2, i);
                newBoard[6][i] = new Piece('w', 6, i);
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
     */
    public Piece getPiece(int row, int col) {
        return board[row][col];
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
}
