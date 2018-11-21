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
    private static final int[][] positionWeightLookup = {{0, 4, 0, 4, 0, 4, 0, 4},
                                                         {4, 0, 3, 0, 3, 0, 3, 0},
                                                         {0, 3, 0, 2, 0, 2, 0, 4},
                                                         {4, 0, 2, 0, 1, 0, 3, 0},
                                                         {0, 3, 0, 1, 0, 2, 0, 4},
                                                         {4, 0, 2, 0, 2, 0, 3, 0},
                                                         {0, 3, 0, 3, 0, 3, 0, 4},
                                                         {4, 0, 4, 0, 4, 0, 4, 0}};

    /**
     * Runs the minimax algorithm on all moves given in the list parameter, and returns the best one
     * @param board the current game state
     * @param depth the tree depth to generate and search
     * @param moves A list of moves to run minimax on. Here this should be all currently available moves for the ai
     * @return the best move to play from the moves list, as evaluated by the minimax algorithm
     */
    public Move play(Board board, int depth, ArrayList<Move> moves) {
        HashMap<Double, Move> scores = new HashMap<>();
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
    private double minimax(Board board, int depth, double alpha, double beta, char color, Move move) {
//        System.out.println(board.toString());
        if (depth == 0 || board.winCheck() != 0) { // if at depth limit or at leaf node
            return complexHeuristic(board, color); // return node value
        }

        if (color == 'w') { // if player == MAX
            double bestValue = Integer.MIN_VALUE;
            ArrayList<Move> children = moveGenerator.findValidMoves(board, color);
            for (Move m : children) { // for each child of node
                double eval = 0;
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
            double bestValue = Integer.MAX_VALUE;
            ArrayList<Move> children = moveGenerator.findValidMoves(board, color);
            for (Move m : children) { // for each child of node
                double eval = 0;
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

        // +2 for pawn, +4 for king
        int whiteState = (board.getWhitePieces() + board.getWhiteKings()) * 2;
        int redState = (board.getRedPieces() + board.getRedKings()) * 2;

        Piece[][] boardArray = board.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) {
                Piece piece = boardArray[i][j];
                if (piece != null && (j == 0 || j == 7)) {
                    if (piece.getColour() == 'w') { // +1 for piece on edge of board
                        whiteState += 1;
                    } else {
                        redState += 1;
                    }
                }
            }
        }

        return (color=='r') ? redState-whiteState : whiteState-redState;
    }

    private int weightedHeuristic(Board board, char color) {
        // TODO: if there is more than one color check, make two heuristics and have one check when the method is called!
        int win = board.winCheck();
        if (win == 1) {
            return color == 'r' ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
        if (win == -1) {
            return color == 'w' ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }

        int redState = 0;
        int whiteState = 0;

        Piece[][] boardArray = board.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) {
                Piece piece = boardArray[i][j];
                if (piece != null) {
                    if (piece.getColour() == 'r') {
                        redState += piece.isKing() ? (5 * positionWeightLookup[i][j]) : (3 * positionWeightLookup[i][j]);
                    } else {
                        whiteState += piece.isKing() ? (5 * positionWeightLookup[i][j]) : (3 * positionWeightLookup[i][j]);
                    }
                }
            }
        }
        return (color=='r') ? redState-whiteState : whiteState-redState;
    }

    private double complexHeuristic(Board board, char color) {
        int win = board.winCheck();
        if (win == 1) {
            return color == 'r' ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
        if (win == -1) {
            return color == 'w' ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }

        double kingFactor = 1.5;
        double cellFactor = 0.75;

        int redCellWeight = 0;
        int whiteCellWeight = 0;

        int redKings = board.getRedKings();
        int whiteKings = board.getWhiteKings();

        int redPieces = board.getRedPieces() - board.getRedKings();
        int whitePieces = board.getWhitePieces() - board.getWhiteKings();

        for (int i = 0; i < 8; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) {
                if (board.getBoard()[i][j] != null) {
                    if (board.getBoard()[i][j].getColour() == 'r') {
                        redCellWeight += positionWeightLookup[i][j];
                    } else {
                        whiteCellWeight += positionWeightLookup[i][j];
                    }
                }
            }
        }


        int trade = 0;
        if (color == 'r') {
            if (board.getRedPieces() > board.getWhitePieces()) {
                trade = 24 - board.getPieces();
            } else {
                trade = 24 + board.getPieces();
            }
//            System.out.println(((redPieces-whitePieces) + (kingFactor * (redKings-whiteKings)) + (cellFactor * (redCellWeight-whiteCellWeight)) * 1000) + trade);
            return ((redPieces-whitePieces) + (kingFactor * (redKings-whiteKings)) + (cellFactor * (redCellWeight-whiteCellWeight)) * 1000) + trade;

        } else {
            if (board.getWhitePieces() > board.getRedPieces()) {
                trade = 24 - board.getPieces();
            } else {
                trade = 24 + board.getPieces();
            }
//            System.out.println(((whitePieces-redPieces) + (kingFactor * (whiteKings-redKings)) + (cellFactor * (whiteCellWeight-redCellWeight)) * 1000) + trade);
            return ((whitePieces-redPieces) + (kingFactor * (whiteKings-redKings)) + (cellFactor * (whiteCellWeight-redCellWeight)) * 1000) + trade;
        }


        /*
        if (color == 'r') {
            System.out.println(((redPieces-whitePieces) + (kingFactor * (redKings-whiteKings)) + (cellFactor * (redCellWeight-whiteCellWeight))) * 1000);
            return (redPieces-whitePieces) + (kingFactor * (redKings-whiteKings)) + (cellFactor * (redCellWeight-whiteCellWeight));
        } else {
            System.out.println(((whitePieces-redPieces) + (kingFactor * (whiteKings-redKings)) + (cellFactor * (whiteCellWeight-redCellWeight))) * 1000);
            return (whitePieces-redPieces) + (kingFactor * (whiteKings-redKings)) + (cellFactor * (whiteCellWeight-redCellWeight));
        }
        */
    }
}
