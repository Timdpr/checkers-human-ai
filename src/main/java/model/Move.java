package main.java.model;

import java.awt.*;

public class Move {

    protected Point origin;
    protected Point destination;

    public Move(Point origin, Point destination) {
        this.origin = origin;
        this.destination = destination;
    }
}
