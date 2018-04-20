package main.java.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class AI {

    private MoveGenerator moveGenerator = new MoveGenerator();

    public Board play(Board board) {
        ArrayList<Move> moves = moveGenerator.findValidMoves(board, 'w');
        board.updateLocation(moves.get(new Random().nextInt(moves.size())));
        return board;
    }
}
