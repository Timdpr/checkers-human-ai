package main.java.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AI {

    private MoveGenerator moveGenerator = new MoveGenerator();

    public Move play(Board board, int depth) {
        Board boardCopy = new Board(board.getBoard());
        ArrayList<Move> moves = moveGenerator.findValidMoves(boardCopy, 'w');
        HashMap<Double, Move> scores = new HashMap<>();

        for (Move move : moves) {
            scores.put(minimax(boardCopy.updateLocation(move), depth, 'w'), move);
        }
        return scores.get(Collections.max(scores.keySet()));
    }

    public double minimax(Board board, int depth, char color) {
        if (depth == 0 || board.winCheck() != 0) {
            return heuristic(board, color);
        }

        if (color == 'w') {
            double bestValue = Double.NEGATIVE_INFINITY;
            for (Move m : moveGenerator.findValidMoves(board, color)) {
                double eval = minimax(getChildBoard(board, m), depth-1, 'r');
                bestValue = Math.max(bestValue, eval);
            }
            return bestValue;
        }

        if (color == 'r') {
            double bestValue = Double.POSITIVE_INFINITY;
            for (Move m : moveGenerator.findValidMoves(board, color)) {
                double eval = minimax(getChildBoard(board, m), depth-1, 'w');
                bestValue = Math.min(bestValue, eval);
            }
            return bestValue;
        }
        System.err.println("Minimax did not return correctly");
        return 0;
    }

    private double heuristic(Board board, char color) {
        int pieceAdvantage = 24;
        Piece[][] pBoard = board.getBoard();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pBoard[i][j] != null) {
                    if (pBoard[i][j].getColour() == color) {
                        pieceAdvantage += 1;
                        if (pBoard[i][j].isKing()) {
                            pieceAdvantage += 1;
                        }
                    } else {
                        pieceAdvantage -= 1;
                        if (pBoard[i][j].isKing()) {
                            pieceAdvantage -= 1;
                        }
                    }
                }
            }
        }
        return pieceAdvantage;
    }

    private Board getChildBoard(Board board, Move move) {
        Board childBoard = new Board(board.getBoard());
        childBoard.updateLocation(move);
        return childBoard;
    }
}
