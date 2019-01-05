package main.java.model;

import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Uses the minimax algorithm with alpha-beta pruning and a heuristic to determine the best available move for the AI.
 *
 * @author tp275
 */
public class AI {

    private final MoveGenerator moveGenerator = new MoveGenerator();
    private static final int[][] positionWeightLookup = {{0, 4, 0, 4, 0, 4, 0, 4},
                                                         {4, 0, 3, 0, 3, 0, 3, 0},
                                                         {0, 3, 0, 2, 0, 2, 0, 4},
                                                         {4, 0, 2, 0, 1, 0, 3, 0},
                                                         {0, 3, 0, 1, 0, 2, 0, 4},
                                                         {4, 0, 2, 0, 2, 0, 3, 0},
                                                         {0, 3, 0, 3, 0, 3, 0, 4},
                                                         {4, 0, 4, 0, 4, 0, 4, 0}};
    public static final int POSITIVE_INFINITY = 2000000000;
    public static final int NEGATIVE_INFINITY = -2000000000;

    /*
    /**
     * Runs the minimax algorithm on all moves given in the list parameter, and returns the best one
     * @param board the current game state
     * @param depth the tree depth to generate and search
     * @param moves A list of moves to run minimax on. Here this should be all currently available moves for the ai
     * @return the best move to play from the moves list, as evaluated by the minimax algorithm
     *
    public Move play(Board board, int depth, ArrayList<Move> moves) {
        HashMap<Double, Move> scores = new HashMap<>();
        for (Move move : moves) {
            scores.put(minimax(board.updateLocation(move), depth, NEGATIVE_INFINITY, POSITIVE_INFINITY, 'w', move), move);
        }
        return scores.get(Collections.max(scores.keySet()));
    }
    */

    public Move playTimeLimited(Board board, int timeLimitSeconds, ArrayList<Move> moves) {
        moves = moveGenerator.updateValidMovesWithJumps(board, moves, 'w');
        LocalTime localTimeLimit = LocalTime.now().plusSeconds(timeLimitSeconds);
        HashMap<Integer, Move> scores = getScores(board, localTimeLimit, moves);
        Move bestMove = scores.get(Collections.max(scores.keySet()));
        System.out.println("Selected move: " + bestMove);
        return bestMove;
    }

    private HashMap<Integer, Move> getScores(Board board, LocalTime localTimeLimit, ArrayList<Move> moves) {
        for (Move move : moves) {
            System.out.print("{" + move + "} ");
        }
        System.out.println();

        HashMap<Integer, Move> scores = new HashMap<>();

        if (moves.size() == 0) {
            System.out.println("No moves to evaluate");
            scores.put(0, new Move(new Point(), new Point()));
            return scores;
        }

        if (moves.size() == 1) {
            System.out.println("Only one move to take!");
            scores.put(1, moves.get(0));
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return scores;
        }

        for (int depth = 4; depth < POSITIVE_INFINITY; depth++) {
            HashMap<Integer, Move> currentDepthScores = new HashMap<>();
            System.out.print("Depth = " + depth + ": ");
            for (Move move : moves) {
                if (LocalTime.now().isBefore(localTimeLimit)) {
                    int result = minimaxJumpUpdate(board.updateLocation(move), depth, NEGATIVE_INFINITY, POSITIVE_INFINITY, 'r', move);
                    currentDepthScores.put(result, move);
                    System.out.print(result + ", ");
                } else {
                    if (!scores.isEmpty()) {
                        System.out.println("Time up!\nBest score: " + Collections.max(scores.keySet()));
                    }
                    return scores;
                }
            }
            scores = currentDepthScores;
            if (!currentDepthScores.isEmpty()) {
                System.out.println("Best score for this depth: " + Collections.max(currentDepthScores.keySet()));
            }
        }
        return null;
    }

    private void checkValue(double value, String valueName) {
        if (value > POSITIVE_INFINITY) {
            System.out.println(valueName + " was above positive infinity");
        } else if (value < NEGATIVE_INFINITY) {
            System.out.println(valueName + " was below negative infinity");
        }
    }

    private void checkValues(double alpha, double beta, int checkNumber) {
        checkValue(alpha, "alpha");
        checkValue(beta, "beta");
    }

    private void checkValues(double alpha, double beta, double bestValue, int checkNumber) {
        checkValue(alpha, "alpha");
        checkValue(beta, "beta");
        checkValue(bestValue, "bestValue");
    }

    private void checkValues(double alpha, double beta, double eval, double bestValue, int checkNumber) {
        checkValue(alpha, "alpha");
        checkValue(beta, "beta");
        checkValue(eval, "eval");
        checkValue(bestValue, "bestValue");
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
    private int minimaxJumpUpdate(Board board, int depth, double alpha, double beta, char color, Move move) {
        if (depth == 0) { // if at depth limit
            return pieceAndRowHeuristic(board);
        }

        int win = board.winCheck();
        if (win != 0) { // if at leaf node
            return win;
        }

        if (color == 'w') { // if player == MAX
            int bestValue = NEGATIVE_INFINITY;
            ArrayList<Move> children = moveGenerator.findValidMoves(board, color);
            if (children.isEmpty()) {
                return NEGATIVE_INFINITY;
            }
            children = moveGenerator.updateValidMovesWithJumps(board, children, color);
            for (Move m : children) { // for each child of node
                int eval;
                Board childBoard = board.updateLocation(m); // (make child)
                // recursively call minimaxJumpUpdate with MIN
                eval = minimaxJumpUpdate(childBoard, depth-1, alpha, beta, 'r', m);
                bestValue = Math.max(bestValue, eval); // best value is max
                alpha = Math.max(alpha, bestValue); // alpha is max
                if (alpha > beta) { // pruning
                    break;
                }
            }
            return bestValue;
        }

        if (color == 'r') { // if player == MIN
            int bestValue = POSITIVE_INFINITY;
            ArrayList<Move> children = moveGenerator.findValidMoves(board, color);
            if (children.isEmpty()) {
                return POSITIVE_INFINITY;
            }
            children = moveGenerator.updateValidMovesWithJumps(board, children, color);
            for (Move m : children) { // for each child of node
                int eval;
                Board childBoard = board.updateLocation(m); // (make child)
                // recursively call minimaxJumpUpdate with MAX
                eval = minimaxJumpUpdate(childBoard, depth-1, alpha, beta, 'w', m);
                bestValue = Math.min(bestValue, eval); // best value is min
                beta = Math.min(beta, bestValue); // beta is min
                if (alpha > beta) { // pruning
                    break;
                }
            }
            return bestValue;
        }
        System.out.println("minimaxJumpUpdate did not return correctly");
        return 0;
    }

    /**
     * The heuristic: a measure of how good the given board state is for the given colour.
     * Currently takes into account whether the player has won and their piece advantage (counting kings as 2)
     * @param board the board state to evaluate
     * @return int, as a measure of how good the given board state is for the given colour
     */
    private int heuristic(Board board) {
        // +2 for pawn, +4 for king
        int whiteState = (board.getWhitePieces() + board.getWhiteKings()) * 2;
        int redState = (board.getRedPieces() + board.getRedKings()) * 2;

//        Piece[][] boardArray = board.getBoard();
//        for (int i = 0; i < 8; i++) {
//            for (int j = (i + 1) % 2; j < 8; j += 2) {
//                Piece piece = boardArray[i][j];
//                if (piece != null && (j == 0 || j == 7)) {
//                    if (piece.getColour() == 'w') { // +1 for piece on edge of board
//                        whiteState += 1;
//                    } else {
//                        redState += 1;
//                    }
//                }
//            }
//        }

        return whiteState - redState;
    }

    private int weightedHeuristic(Board board) {
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
        return whiteState - redState;
    }

    private int pieceAndRowHeuristic(Board board) {
        int redState = 0;
        int whiteState = 0;

        Piece[][] boardArray = board.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) {
                Piece piece = boardArray[i][j];
                if (piece != null) {
                    if (piece.getColour() == 'r') {
                        redState += piece.isKing() ? 14 : (5 + (7 - i));
                    } else {
                        whiteState += piece.isKing() ? 14 : (5 + i);
                    }
                }
            }
        }
        return whiteState - redState;
    }

    private int pieceAndRowAndWeightedHeuristic(Board board) {
        int redState = 0;
        int whiteState = 0;

        Piece[][] boardArray = board.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) {
                Piece piece = boardArray[i][j];
                if (piece != null) {
                    if (piece.getColour() == 'r') {
                        redState += piece.isKing() ? (14 * positionWeightLookup[i][j]) : ((5 + (8 - i)) * positionWeightLookup[i][j]);
                    } else {
                        whiteState += piece.isKing() ? (14 * positionWeightLookup[i][j]) : ((5 + i) * positionWeightLookup[i][j]);
                    }
                }
            }
        }
        return whiteState - redState;
    }

    private int complexHeuristic(Board board, char color) {
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

        int trade;

        if (board.getRedPieces() > board.getWhitePieces()) {
            trade = 24 - board.getPieces();
        } else {
            trade = 24 + board.getPieces();
        }
        return (int) ((whitePieces-redPieces) + (kingFactor * (whiteKings-redKings)) + (cellFactor * (whiteCellWeight-redCellWeight)) * 1000) + trade;

    }
}
