package main.java.model;

import java.util.ArrayList;
import java.util.Random;

public class AI {

    private MoveGenerator moveGenerator = new MoveGenerator();
    private Move lastMove;

    public Board play(Board board) {
        ArrayList<Move> moves = moveGenerator.findValidMoves(board, 'w');
        lastMove = moves.get(new Random().nextInt(moves.size()));
        board.updateLocation(lastMove);
        return board;
    }

    public Move getLastMove() {
        return lastMove;
    }
}
