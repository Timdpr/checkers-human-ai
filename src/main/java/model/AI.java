package main.java.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Uses the minimax algorithm with alpha-beta pruning and a heuristic to determine the best available move for the AI.
 *
 * @author tp275
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
     * @param board the game state
     * @param depth the max depth to generate the tree
     * @param alpha alpha pruning parameter
     * @param beta beta pruning parameter
     * @param color essentially MIN/MAX player, here 'w' == MAX
     * @param move the last move, used in reversing moves
     * @return the best score possible for the AI (given that the human plays with the same technique!)
     */
    private int minimax(Board board, int depth, int alpha, int beta, char color, Move move) {
        if (depth == 0 || board.winCheck() != 0) { // if at depth limit or at leaf node
            return heuristic(board, color); // return node value
        }

        if (color == 'w') { // if player == MAX
            int bestValue = Integer.MIN_VALUE;
            ArrayList<Move> children = moveGenerator.findValidMoves(board, color);
            for (Move m : children) { // for each child of node
                int eval = 0;
                Board childBoard = board.updateLocation(m); // (make child)
                ArrayList<Move> multiMoves = moveGenerator.detectMultiMove(childBoard, 'w', m.getDestination());
                if (multiMoves.size() > 0) { // if there is a multimove, recursively call MAX again...
                    for (Move mm : multiMoves) {
                        eval = minimax(childBoard.updateLocation(mm), depth-1, alpha, beta, 'w', mm);
                        board.reverseMove(move, 'w'); // reverse move
                    }
                } else { // ...otherwise, recursively call minimax with MIN
                    eval = minimax(childBoard, depth-1, alpha, beta, 'r', m);
                    board.reverseMove(move, 'r'); // reverse move
                }
                bestValue = Math.max(bestValue, eval); // best value is max
                alpha = Math.max(alpha, bestValue); // alpha is max
                if (alpha > beta) { // pruning
                    break;
                }
            }
            return bestValue;
        }

        if (color == 'r') { // if player == MIN
            int bestValue = Integer.MAX_VALUE;
            ArrayList<Move> children = moveGenerator.findValidMoves(board, color);
            for (Move m : children) { // for each child of node
                int eval = 0;
                Board childBoard = board.updateLocation(m); // (make child)
                ArrayList<Move> multiMoves = moveGenerator.detectMultiMove(childBoard, 'r', m.getDestination());
                if (multiMoves.size() > 0) { // if there is a multimove, recursively call MIN again...
                    for (Move mm : multiMoves) {
                        eval = minimax(childBoard.updateLocation(mm), depth-1, alpha, beta, 'r', mm); // recursive call
                        board.reverseMove(move, 'r'); // reverse move
                    }
                } else {  // ...otherwise, recursively call minimax with MAX
                    eval = minimax(childBoard, depth-1, alpha, beta, 'w', m);
                    board.reverseMove(move, 'w'); // reverse move
                }
                bestValue = Math.min(bestValue, eval); // best value is min
                beta = Math.min(beta, bestValue); // beta is min
                if (alpha > beta) { // pruning
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
