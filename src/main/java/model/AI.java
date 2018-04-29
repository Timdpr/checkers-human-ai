package main.java.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AI {

    private MoveGenerator moveGenerator = new MoveGenerator();

    public Move play(Board board, int depth, ArrayList<Move> moves) {
        Board boardCopy = new Board(board.getBoard());
        HashMap<Integer, Move> scores = new HashMap<>();

        for (Move move : moves) {
            scores.put(minimax(boardCopy.updateLocation(move), depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 'w'), move);
        }
        return scores.get(Collections.max(scores.keySet()));
    }

    private int minimax(Board board, int depth, int alpha, int beta, char color) {
        if (depth == 0 || board.winCheck() != 0) {
            return heuristic(board, color);
        }

        if (color == 'w') {
            int bestValue = Integer.MIN_VALUE;
            for (Move m : moveGenerator.findValidMoves(board, color)) {
                Board childBoard = getChildBoard(board, m);
                int eval;
                if (moveGenerator.detectMultiMove(childBoard, 'r', m.getDestination())) {
                    eval = minimax(childBoard, depth-1, alpha, beta, 'w');
                } else {
                    eval = minimax(childBoard, depth-1, alpha, beta, 'r');
                }
                bestValue = Math.max(bestValue, eval);
                alpha = Math.max(alpha, bestValue);
                if (alpha > beta) {
                    break;
                }
            }
            return bestValue;
        }

        if (color == 'r') {
            int bestValue = Integer.MAX_VALUE;
            for (Move m : moveGenerator.findValidMoves(board, color)) {
                Board childBoard = getChildBoard(board, m);
                int eval;
                if (moveGenerator.detectMultiMove(childBoard, 'r', m.getDestination())) {
                    eval = minimax(childBoard, depth-1, alpha, beta, 'r');
                } else {
                    eval = minimax(childBoard, depth-1, alpha, beta, 'w');
                }
                bestValue = Math.min(bestValue, eval);
                beta = Math.min(beta, bestValue);
                if (alpha > beta) {
                    break;
                }
            }
            return bestValue;
        }
        System.out.println("Minimax did not return correctly");
        return 0;
    }

    // TODO: Better heuristic! Weight kings, weight pieces being at the sides, etc...
    private int heuristic(Board board, char color) {
        int whiteState = 0;
        int redState = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) {
                Piece piece = board.getPiece(i, j);
                if (piece != null) {
                    if (piece.getColour() == 'r') {
                        redState++;
                    } else {
                        whiteState++;
                    }
                }
            }
        }
        int heur = (color=='r') ? redState-whiteState : whiteState-redState;
        return heur;
    }

    private Board getChildBoard(Board board, Move move) {
        Board childBoard = new Board(board.getBoard());
        childBoard = childBoard.updateLocation(move);
        return childBoard;
    }
}
