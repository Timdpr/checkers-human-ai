package main.java.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AI {

    private MoveGenerator moveGenerator = new MoveGenerator();

    public Move play(Board board, int depth, ArrayList<Move> moves) {
        HashMap<Integer, Move> scores = new HashMap<>();
        for (Move move : moves) {
            scores.put(minimax(board.updateLocation(move), depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 'w', move), move);
        }
        return scores.get(Collections.max(scores.keySet()));
    }

    private int minimax(Board board, int depth, int alpha, int beta, char color, Move move) {
        if (depth == 0 || board.winCheck() != 0) {
            return heuristic(board, color);
        }

        if (color == 'w') {
            int bestValue = Integer.MIN_VALUE;
            ArrayList<Move> children = moveGenerator.findValidMoves(board, color);
            for (Move m : children) {
                int eval = 0;
                Board childBoard = board.updateLocation(m);
                ArrayList<Move> multiMoves = moveGenerator.detectMultiMove(childBoard, 'w', m.getDestination());
                if (multiMoves.size() > 0) {
                    for (Move mm : multiMoves) {
                        eval = minimax(childBoard.updateLocation(mm), depth-1, alpha, beta, 'w', mm);
                        board.reverseMove(move, 'w');
                    }
                } else {
                    eval = minimax(childBoard, depth-1, alpha, beta, 'r', m);
                    board.reverseMove(move, 'r');
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
            ArrayList<Move> children = moveGenerator.findValidMoves(board, color);
            for (Move m : children) {
                int eval = 0;
                Board childBoard = board.updateLocation(m);
                ArrayList<Move> multiMoves = moveGenerator.detectMultiMove(childBoard, 'r', m.getDestination());
                if (multiMoves.size() > 0) {
                    for (Move mm : multiMoves) {
                        eval = minimax(childBoard.updateLocation(mm), depth-1, alpha, beta, 'r', mm);
                        board.reverseMove(move, 'r');
                    }
                } else {
                    eval = minimax(childBoard, depth-1, alpha, beta, 'w', m);
                    board.reverseMove(move, 'w');
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
        int whiteState = board.getWhitePieces();
        int redState = board.getRedPieces();

        int heur = (color=='r') ? redState-whiteState : whiteState-redState;
        return heur;
    }
}
