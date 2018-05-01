package main.java.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 */
public class AI {

    private MoveGenerator moveGenerator = new MoveGenerator();

    /**
     * Runs the minimax algorithm on all moves given in the list parameter, and returns the best one
     * @param board the current game state
     * @param depth the tree depth to generate and search
     * @param moves A list of moves to run minimax on. Here this should be all currently available moves for the ai
     * @return the best move to play from the moves list, as evaluated by the minimax algorithm
     */
    public Move play(Board board, int depth, ArrayList<Move> moves) {
        HashMap<Integer, Move> scores = new HashMap<>();
        for (Move move : moves) {
            scores.put(minimax(board.updateLocation(move), depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 'w', move), move);
        }
        return scores.get(Collections.max(scores.keySet()));
    }

    /**
     * The minimax algorithm, including alpha-beta pruning.
     * @param board
     * @param depth
     * @param alpha
     * @param beta
     * @param color
     * @param move
     * @return
     */
    private int minimax(Board board, int depth, int alpha, int beta, char color, Move move) {
        board.printBoard();
        System.out.println();
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

    /**
     * The heuristic: a measure of how good the given board state is for the given colour.
     * Currently takes into account whether the player has won and their piece advantage (counting kings as 2)
     * @param board the board state to evaluate
     * @param color the side to evaluate for
     * @return int, as a measure of how good the given board state is for the given colour
     */
    private int heuristic(Board board, char color) {
        int win = board.winCheck();
        if (win == 1) {
            return color == 'r' ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
        if (win == -1) {
            return color == 'w' ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }

        int whiteState = board.getWhitePieces() + (board.getWhiteKings()*2);
        int redState = board.getRedPieces() + (board.getRedKings()*2);

        return (color=='r') ? redState-whiteState : whiteState-redState;
    }
}
