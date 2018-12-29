package main.java.model;

import java.awt.*;

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
     * Creates and sets up pieces on the board in their initial state, and initialises piece counts
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
     * Also sets piece counts as they should be
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
     * Assume Piece is being added, so increase relevant piececounts
     * @param oldPiece the Piece to update with
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

    public Piece[][] deepCopy(Piece[][] original) {
        Piece[][] result = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = (i+1)%2; j < 8; j+=2) {
                Piece oldPiece = original[i][j];
                if (oldPiece != null) {
                    result[i][j] = new Piece(oldPiece.getColour(), oldPiece.isKing());
                }
            }
        }
        return result;
    }

    /**
     * Returns a copy of the current board after being updated with the given move.
     * This also includes updating kings and piece counts.
     * @param move the move to update the board with
     * @return a copy of the current board after being updated with the given move
     */
    public Board updateLocation(Move move) {
        Piece[][] boardCopy = deepCopy(board);
        // Store piece at origin and delete it from board
        Piece originPiece = boardCopy[move.origin.x][move.origin.y];
        if (move.kingPiece) {
            if (originPiece != null) {
                originPiece.setKing();
            }
        }
        boardCopy[move.origin.x][move.origin.y] = null;
        // If there is an intermediate piece (in a jump), update piece counts then remove it
        if (move.hasPieceToRemove()) {
            for (Point pieceToRemove : move.getPiecesToRemove()) {
//                decreaseCounts(boardCopy[pieceToRemove.x][pieceToRemove.y]);
                boardCopy[pieceToRemove.x][pieceToRemove.y] = null;
            }
        }
        // Now insert the original piece at the destination
        boardCopy[move.destination.x][move.destination.y] = originPiece;
        return new Board(boardCopy);
    }

    /**
     * Assume Piece is being removed, so decrease relevant piececounts
     * @param oldPiece the Piece to update with
     */
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

    /**
     * Reverses the given move on the current board
     * @param move the Move to reverse
     * @param color the colour of the moving piece
     */
    public void reverseMove(Move move, char color) {
        Piece dest = this.board[move.destination.x][move.destination.y];
        this.board[move.origin.x][move.origin.y] = dest;
        this.board[move.destination.x][move.destination.y] = null;
        if (move.hasPieceToRemove()) {
            for (Point pieceToRemove : move.getPiecesToRemove()) {
                if (color == 'r') {
                    Piece pieceToAdd = new Piece('w');
                    this.board[pieceToRemove.x][pieceToRemove.y] = pieceToAdd;
                    increaseCounts(pieceToAdd);
                } else {
                    Piece pieceToAdd = new Piece('r');
                    this.board[pieceToRemove.x][pieceToRemove.y] = pieceToAdd;
                    increaseCounts(pieceToAdd);
                }
            }
        }
    }

    /**
     * Checks for a win for either team by checking piececounts
     * @return 1 if red win, -1 if white win, else 0
     */
    public int winCheck() {
        if (this.whitePieces == 0) {
            return AI.NEGATIVE_INFINITY;
        } else if (this.redPieces == 0) {
            return AI.POSITIVE_INFINITY;
        }
        return 0;
    }

    public int winCheck(MoveGenerator moveGenerator) {
        if (this.whitePieces == 0 || moveGenerator.findValidMoves(this, 'w').size() == 0) {
            return AI.NEGATIVE_INFINITY;
        } else if (this.redPieces == 0 || moveGenerator.findValidMoves(this, 'r').size() == 0) {
            return AI.POSITIVE_INFINITY;
        }
        return 0;
    }

    /**
     * @return total # pieces on board
     */
    public int getPieces() {
        return whitePieces + redPieces;
    }

    /**
     * @return # white pieces on board
     */
    public int getWhitePieces() {
        return whitePieces;
    }

    /**
     * @return # red pieces on board
     */
    public int getRedPieces() {
        return redPieces;
    }

    /**
     * @return # white kings on board
     */
    public int getWhiteKings() {
        return whiteKings;
    }

    /**
     * @return # red kings on board
     */
    public int getRedKings() {
        return redKings;
    }

    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece == null) {
                    boardString.append('-');
                } else {
                    if (piece.isKing()) {
                        boardString.append(Character.toUpperCase(piece.getColour()));
                    } else {
                        boardString.append(piece.getColour());
                    }
                }
                boardString.append(" ");
            }
            boardString.append("\n");
        }
        return boardString.toString();
    }
}
