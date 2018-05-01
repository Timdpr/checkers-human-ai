package main.java.model;

/**
 * The state representation: an 8x8 2d array holding Piece objects.
 *
 * @author tp275
 */
public class Board {

    private Piece[][] board;
    private int whitePieces;
    private int redPieces;
    private int whiteKings;
    private int redKings;

    /**
     * Creates and sets up pieces on the board in their initial state
     */
    public Board() {
        this.board = getInitialBoard();
        this.whitePieces = 12;
        this.redPieces = 12;
        this.whiteKings = 0;
        this.redKings = 0;
    }

    /**
     * Creates a new Board object with it's board as the given Piece[][] - useful for copying
     * @param board the state representation for the board to hold
     */
    public Board(Piece[][] board) {
        this.redPieces = 0;
        this.whitePieces = 0;
        this.board = new Piece[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = (i+1)%2; j < 8; j+=2) {
                Piece oldPiece = board[i][j];
                if (oldPiece != null) {
                    increaseCounts(oldPiece);
                    this.board[i][j] = new Piece(oldPiece.getColour(), oldPiece.isKing());
                }
            }
        }
    }

    /**
     * @param oldPiece
     */
    private void increaseCounts(Piece oldPiece) {
        if (oldPiece.getColour() == 'r') {
            if (oldPiece.isKing()) {
                this.redKings++;
            }
            this.redPieces++;
        } else {
            if (oldPiece.isKing()) {
                this.whiteKings++;
            }
            this.whitePieces++;
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
                newBoard[1][i] = new Piece('w');
                newBoard[5][i] = new Piece('r');
                newBoard[7][i] = new Piece('r');
            } else {
                newBoard[0][i] = new Piece('w');
                newBoard[2][i] = new Piece('w');
                newBoard[6][i] = new Piece('r');
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
     * Returns the Piece at the given row, column location in the board array
     * @param row the row number of the wanted piece
     * @param col the column number of the wanted piece
     * @return the Piece at the selected row, column location in the board array
     */
    public Piece getPiece(int row, int col) {
        return board[row][col];
    }

    /**
     * Returns a copy of the current board after being updated with the given move.
     * This also includes updating kings and piece counts.
     * @param move the move to update the board with
     * @return a copy of the current board after being updated with the given move
     */
    public Board updateLocation(Move move) {
        Piece[][] boardCopy = new Board(board).getBoard();
        // Store piece at origin and delete it from board
        Piece originPiece = boardCopy[move.origin.x][move.origin.y];
        if (move.kingPiece) {
            if (originPiece != null) {
                originPiece.setKing(true);
            }
        }
        boardCopy[move.origin.x][move.origin.y] = null;
        // If there is an intermediate piece (in a jump), update piece counts then remove it
        if (move.hasPieceToRemove()) {
            decreaseCounts(boardCopy[move.pieceToRemove.x][move.pieceToRemove.y]);
            boardCopy[move.pieceToRemove.x][move.pieceToRemove.y] = null;
        }
        // Now insert the original piece at the destination
        boardCopy[move.destination.x][move.destination.y] = originPiece;
        return new Board(boardCopy);
    }

    private void decreaseCounts(Piece oldPiece) {
        if (oldPiece != null) {
            if (oldPiece.getColour() == 'r') {
                if (oldPiece.isKing()) {
                    this.redKings--;
                }
                this.redPieces--;
            } else {
                if (oldPiece.isKing()) {
                    this.whiteKings--;
                }
                this.whitePieces--;
            }
        }
    }

    public void reverseMove(Move move, char color) {
        Piece dest = this.board[move.destination.x][move.destination.y];
        this.board[move.origin.x][move.origin.y] = dest;
        this.board[move.destination.x][move.destination.y] = null;
        if (move.hasPieceToRemove()) {
            if (color=='r') {
                this.board[move.pieceToRemove.x][move.pieceToRemove.y] = new Piece('w');
            } else {
                this.board[move.pieceToRemove.x][move.pieceToRemove.y] = new Piece('r');
            }
        }
    }

    /**
     * Prints a text representation of the current board
     */
    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    Piece p = getPiece(i, j);
                    if (p.isKing()) {
                        if (p.getColour() == 'r') {
                            System.out.print("R ");
                        } else {
                            System.out.print("W ");
                        }
                    } else {
                        System.out.print(board[i][j].getColour() + " ");
                    }
                } else {
                    System.out.print("- ");
                }
            } System.out.println();
        }
    }

    public int winCheck() {
        if (this.whitePieces == 0) {
            return 1;
        } else if (this.redPieces == 0) {
            return -1;
        }
        return 0;
    }

    public int getWhitePieces() {
        return whitePieces;
    }

    public int getRedPieces() {
        return redPieces;
    }

    public int getWhiteKings() {
        return whiteKings;
    }

    public int getRedKings() {
        return redKings;
    }
}
