package main.java.model;

import java.awt.*;

public class AI {

    public Board play(Board board) {
        board.updateLocation(new Move(new Point(2,3), new Point(4,3)));
        return board;
    }
}
